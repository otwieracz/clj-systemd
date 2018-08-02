(ns clj-systemd.manager
  (:require [clj-systemd.systemd :as systemd]
            [clojure.spec.alpha :as spec])
  (:import (de.thjom.java.systemd Manager)))

(spec/def ::service-name string?)

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
   (.getService (get-manager instance-type) service-name))
  ([service-name]
   (get-service :system service-name)))

