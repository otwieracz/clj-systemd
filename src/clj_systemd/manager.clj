(ns clj-systemd.manager
  (:require [clojure.spec.alpha :as spec]
            [clj-systemd.systemd :as systemd]
            [clj-systemd.service :as service]
            )
  (:import (de.thjom.java.systemd Manager)))

(spec/def ::service-name string?)

(spec/def ::start-mode #{:replace :fail :isolate :ignore-dependencies :ignore-requirements})

(spec/def ::stop-mode #{:replace :fail :ignore-dependencies :ignore-requirements})


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

(defn- start-stop-unit
  "Common method for unit start/stop"
  [instance-type method service-name start-stop-mode]
  {:pre [(spec/valid? ::service-name service-name)
         (spec/valid? #{:start :stop :restart} method)]}
  (case method
    :start (.startUnit (get-manager instance-type) service-name (name start-stop-mode))
    :stop (.stopUnit (get-manager instance-type) service-name (name start-stop-mode))
    :restart (.restartUnit (get-manager instance-type) service-name (name start-stop-mode))))

(defn start-unit
  "Start unit `UNIT-NAME` with mode `START-MODE`"
  ([instance-type service-name start-mode]
   {:pre [(spec/valid? ::start-mode start-mode)]}
   (start-stop-unit instance-type :start service-name start-mode))
  ([service-name start-mode]
   (start-unit :system service-name start-mode)))

(defn stop-unit
  "Stop unit `UNIT-NAME` with mode `STOP-MODE`"
  ([instance-type service-name stop-mode]
   {:pre [(spec/valid? ::stop-mode stop-mode)]}
   (start-stop-unit instance-type :stop service-name stop-mode))
  ([service-name stop-mode]
   (start-unit :system service-name stop-mode)))

(defn restart-unit
  "Restart unit `UNIT-NAME` with mode `RESTART-MODE`"
  ([instance-type service-name start-mode]
   {:pre [(spec/valid? ::start-mode start-mode)]}
   (start-stop-unit instance-type :restart service-name start-mode))
  ([service-name start-mode]
   (start-unit :system service-name start-mode)))

(defn reload
  "Reload systemd daemon"
  ([instance-type]
   (.reload (get-manager instance-type)))
  ([]
   (reload :system)))
