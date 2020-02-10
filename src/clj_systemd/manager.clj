(ns clj-systemd.manager
  (:require [clojure.spec.alpha :as spec]
            [clj-systemd.service :as service]
            [clj-systemd.timer :as timer]
            [clj-systemd.unit :as unit])
  (:import (de.thjom.java.systemd Manager)))

(spec/def ::unit-name string?)
(spec/def ::service-name string?)
(spec/def ::timer-name string?)

(spec/def ::start-mode #{:replace :fail :isolate :ignore-dependencies :ignore-requirements})

(spec/def ::stop-mode #{:replace :fail :ignore-dependencies :ignore-requirements})

(spec/def ::unit-or-unit-name (spec/or :unit ::unit/unit
                                       :unit-name ::unit-name))

(defn get-manager
  "Get instance of `Manager` for systemd instance"
  ([systemd]
   (.getManager systemd)))

(defn get-service
  "Get Service `SERVICE-NAME`"
  ([manager service-name]
   {:pre [(spec/valid? ::service-name service-name)]}
   (service/to-service (.getService manager service-name))))

(defn get-unit
  "Get Unit `UNIT-NAME`"
  ([manager unit-name]
   {:pre [(spec/valid? ::unit-name unit-name)]}
   (unit/to-unit (.getUnit manager unit-name))))

(defn get-timer
  "Get Timer `TIMER-NAME`"
  ([manager timer-name]
   {:pre [(spec/valid? ::timer-name timer-name)]}
   (timer/to-timer (.getTimer manager timer-name))))

(defn- start-stop-unit
  "Common method for unit start/stop"
  [manager method unit start-stop-mode]
  {:pre [(spec/valid? ::unit-or-unit-name unit)
         (spec/valid? #{:start :stop :restart} method)]}
  (let [unit-name (case (first (spec/conform ::unit-or-unit-name unit))
                    :unit-name unit
                    :unit (:id unit))
        start-stop-mode-keyword (name start-stop-mode)]
    (case method
      :start (.startUnit manager unit-name start-stop-mode-keyword)
      :stop (.stopUnit manager unit-name start-stop-mode-keyword)
      :restart (.restartUnit manager unit-name start-stop-mode-keyword))))

(defn start-unit
  "Start unit `UNIT-NAME` with mode `START-MODE`"
  ([manager unit start-mode]
   {:pre [(spec/valid? ::start-mode start-mode)]}
   (start-stop-unit manager :start unit start-mode))
  ([manager unit]
   (start-unit manager unit :fail)))

(defn stop-unit
  "Stop unit `UNIT-NAME` with mode `STOP-MODE`"
  ([manager unit stop-mode]
   {:pre [(spec/valid? ::stop-mode stop-mode)]}
   (start-stop-unit manager :stop unit stop-mode))
  ([manager unit]
   (stop-unit manager unit :fail)))

(defn restart-unit
  "Restart unit `UNIT-NAME` with mode `RESTART-MODE`"
  ([manager unit start-mode]
   {:pre [(spec/valid? ::start-mode start-mode)]}
   (start-stop-unit manager :restart unit start-mode))
  ([manager unit]
   (restart-unit manager unit :fail)))

(defn reload
  "Reload systemd daemon"
  ([manager]
   (.reload manager)))
