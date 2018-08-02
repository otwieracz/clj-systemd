(ns clj-systemd.systemd
  (:require [clojure.spec.alpha :as spec])
  (:import (de.thjom.java.systemd Systemd Systemd$InstanceType)))

(spec/def ::instance-type #{:user :system})

(defn- get-instance-type
  [instance-type]
  {:pre [(spec/valid? ::instance-type instance-type)]}
  (case instance-type
    :user Systemd$InstanceType/USER
    :system Systemd$InstanceType/SYSTEM))

(defn get-systemd
  "Get Systemd instance"
  [instance-type]
  (Systemd/get (get-instance-type instance-type)))

(defn disconnect
  "Disconnect from Systemd"
  ([instance-type]
   (Systemd/disconnect (get-instance-type instance-type)))
  ([]
   (Systemd/disconnect)))

(defn disconnect-all
  "Disconnect all connections from Systemd"
  []
  (Systemd/disconnectAll))


