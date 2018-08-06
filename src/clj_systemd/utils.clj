(ns clj-systemd.utils
  (:require [clj-time.coerce :as time-coerce]))

(defn usec-to-time
  [usec]
  (time-coerce/from-long (long (/ usec (Math/pow 10 6)))))

