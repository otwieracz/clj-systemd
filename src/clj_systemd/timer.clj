(ns clj-systemd.timer
  (:require [clojure.spec.alpha :as spec]
            [clj-time.core :as time]
            [clj-systemd.unit :as unit]
            [clj-systemd.utils :as utils]
            )
  (:import (de.thjom.java.systemd Timer)))

;; ACCURACY_USEC = "AccuracyUSec";
;; LAST_TRIGGER_USEC = "LastTriggerUSec";
;; LAST_TRIGGER_USEC_MONOTONIC = "LastTriggerUSecMonotonic";
;; NEXT_ELAPSE_USEC_MONOTONIC = "NextElapseUSecMonotonic";
;; NEXT_ELAPSE_USEC_REALTIME = "NextElapseUSecRealtime";
;; PERSISTENT = "Persistent";
;; RANDOMIZED_DELAY_USEC = "RandomizedDelayUSec";
;; REMAIN_AFTER_ELAPSE = "RemainAfterElapse";
;; RESULT = "Result";
(spec/def ::unit string?)
;; TIMERS_CALENDAR = "TimersCalendar";
;; TIMERS_MONOTONIC = "TimersMonotonic";
;; WAKE_SYSTEM = "WakeSystem";


(spec/def ::timer-instance #(= (type %) de.thjom.java.systemd.Timer))

(spec/def ::timer (spec/merge ::unit/unit
                              (spec/keys :req-un [::unit])))

(defn- parse-time
  "Parse systemd time like: (...) contains the next elapsation point on the CLOCK_REALTIME clock in usec since the epoch, or 0 if this timer event does not include at least one calendar event. "
  [usec]
  (if (zero? usec)
    nil
    (utils/usec-to-time usec)))

(defn to-timer
  "Convert `TIMER-INSTANCE` to hash-map"
  [timer-instance]
  {:pre [(spec/valid? ::timer-instance timer-instance)]
   :post [(spec/valid? ::timer %)]}
  (let [unit (unit/to-unit timer-instance)
        when-active (fn [x] (when (= (:active-state unit) :active) x))]
    (merge unit
           {:unit (.getUnit timer-instance)
            ;; Time values make sense only when timer is `:ACTIVE`
            :next-elapse-time-monotonic (when-active (.getNextElapseUSecMonotonic timer-instance))
            :next-elapse-time-realtime (when-active (.getNextElapseUSecRealtime timer-instance))
            :last-trigger-time (when-active (.getLastTriggerUSec timer-instance))
            :last-trigger-time-monotonic (when-active (.getLastTriggerUSecMonotonic timer-instance))})))
