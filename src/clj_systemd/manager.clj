(ns clj-systemd.manager
  (:require [clojure.spec.alpha :as spec]
            [clj-systemd.systemd :as systemd]
            [clj-systemd.service :as service]
            [clj-systemd.timer :as timer]
            [clj-systemd.unit :as unit]
            )
  (:import (de.thjom.java.systemd Manager)))

(spec/def ::unit-name string?)
(spec/def ::service-name string?)
(spec/def ::timer-name string?)

(spec/def ::start-mode #{:replace :fail :isolate :ignore-dependencies :ignore-requirements})

(spec/def ::stop-mode #{:replace :fail :ignore-dependencies :ignore-requirements})

(spec/def ::unit-or-unit-name (spec/or :unit ::unit/unit
                                       :unit-name ::unit-name))

(defn- get-manager
  "Get instance of `Manager`"
  ([instance-type]
   (.getManager (systemd/get-systemd instance-type)))
  ([]
   (get-manager :system)))

(defn get-service
  "Get Service `SERVICE-NAME`"
  ([instance-type service-name]
   {:pre [(spec/valid? ::service-name service-name)]}
   (service/to-service (.getService (get-manager instance-type) service-name)))
  ([service-name]
   (get-service :system service-name)))

(defn get-unit
  "Get Unit `UNIT-NAME`"
  ([instance-type unit-name]
   {:pre [(spec/valid? ::unit-name unit-name)]}
   (unit/to-unit (.getUnit (get-manager instance-type) unit-name)))
  ([unit-name]
   (get-unit :system unit-name)))

(defn get-timer
  "Get Timer `TIMER-NAME`"
  ([instance-type timer-name]
   {:pre [(spec/valid? ::timer-name timer-name)]}
   (timer/to-timer (.getTimer (get-manager instance-type) timer-name)))
  ([timer-name]
   (get-timer :system timer-name)))

(defn- start-stop-unit
  "Common method for unit start/stop"
  [instance-type method unit start-stop-mode]
  {:pre [(spec/valid? ::unit-or-unit-name unit)
         (spec/valid? #{:start :stop :restart} method)]}
  (let [unit-name (case (first (spec/conform ::unit-or-unit-name unit))
                    :unit-name unit
                    :unit (:id unit))
        manager (get-manager instance-type)
        start-stop-mode-keyword (name start-stop-mode)]
    (case method
      :start (.startUnit manager unit-name start-stop-mode-keyword)
      :stop (.stopUnit manager unit-name start-stop-mode-keyword)
      :restart (.restartUnit manager unit-name start-stop-mode-keyword))))

(defn start-unit
  "Start unit `UNIT-NAME` with mode `START-MODE`"
  ([instance-type unit start-mode]
   {:pre [(spec/valid? ::start-mode start-mode)]}
   (start-stop-unit instance-type :start unit start-mode))
  ([unit start-mode]
   (start-unit :system unit start-mode))
  ([unit]
   (start-unit :system unit :fail)))

(defn stop-unit
  "Stop unit `UNIT-NAME` with mode `STOP-MODE`"
  ([instance-type unit stop-mode]
   {:pre [(spec/valid? ::stop-mode stop-mode)]}
   (start-stop-unit instance-type :stop unit stop-mode))
  ([unit stop-mode]
   (start-unit :system unit stop-mode))
  ([unit]
   (start-unit :system unit :fail)))

(defn restart-unit
  "Restart unit `UNIT-NAME` with mode `RESTART-MODE`"
  ([instance-type unit start-mode]
   {:pre [(spec/valid? ::start-mode start-mode)]}
   (start-stop-unit instance-type :restart unit start-mode))
  ([unit start-mode]
   (start-unit :system unit start-mode))
  ([unit]
   (start-unit :system unit :fail)))

(defn reload
  "Reload systemd daemon"
  ([instance-type]
   (.reload (get-manager instance-type)))
  ([]
   (reload :system)))
