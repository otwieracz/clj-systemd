(ns clj-systemd.service-test
  (:require [clj-systemd.service :as sut]
            [clojure.test :refer :all]))

(deftest environment-to-map-test "testing environment to hash-map conversion"
  (is (= {"GVFS_DISABLE_FUSE" "1" "GIO_USE_VFS" "local" "GVFS_REMOTE_VOLUME_MONITOR_IGNORE" "1"}
         (#'clj-systemd.service/environment-to-map ["GVFS_DISABLE_FUSE=1" "GIO_USE_VFS=local" "GVFS_REMOTE_VOLUME_MONITOR_IGNORE=1"]))))
