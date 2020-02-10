(ns clj-systemd.component
  (:require [com.stuartsierra.component :as component]
            [clojure.spec.alpha :as spec]
            [clj-systemd.systemd :refer [get-systemd disconnect] :as systemd]
            [clj-systemd.manager :refer [get-manager]]))

;; According to https://github.com/thjomnx/java-systemd/wiki/Basic-usage
;; 
;; > Thus at first a connection to either the system or the user instance is 
;; > required. Both can be achieved via the Systemd class. The class is 
;; > implemented as a singleton for each system and user instance which means
;; > that subsequent calls to the particular get () methods will return the
;; > same instance. This design is a direct consequence of the singleton
;; > implementation of the DBusConnection class which comes with dbus-java.
;;
;; In order to address this issue, clj-systemd will track number of instances
;; created for each instance-type and disconnect from specific systemd
;; instance only when counter reaches zero

(defonce instances (atom {:user 0 :system 0}))

(defrecord Systemd [instance-type]
  component/Lifecycle
  (start [component]
    (let [systemd (get-systemd instance-type)
          manager (get-manager systemd)]
      ;; In case of failure, Java methods called from get-{systemd,manager} will throw
      ;; exceptions preventing further code execution. If we get there, we assume it is fine
      ;; and we can count new instance
      (swap! instances #(update % instance-type inc))
      (merge component
             {:systemd systemd
              :manager manager})))
  (stop [component]
    (swap! instances
           ;; this code is executed inside `swap!` to guarantee thread safety
           ;; if this is last instance, disconnect and decrease counter
           #(update % instance-type (fn [n]
                                      (when (= 1 n) (disconnect instance-type))
                                      (dec n))))
    ;; when there are no other instances left, disconnect
    (-> (dissoc component :systemd)
        (dissoc :manager))))

(defn new-systemd [& {:keys [instance-type]}]
  {:pre [(spec/valid? ::systemd/instance-type instance-type)]}
  (map->Systemd {:instance-type instance-type}))