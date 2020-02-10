(defproject clj-systemd "0.2.0"
  :description "Clojure interface to systemd"
  :url "https://github.com/otwieracz/clj-systemd"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.github.thjomnx/java-systemd "1.1.0"]
                 [me.raynes/fs "1.4.6"]
                 [clj-time "0.14.4"]
                 [org.danielsz/system "0.4.5"]
                 [com.stuartsierra/component "0.4.0"]]
:profile {:dev {:aot [user com.stuartsierra.component]}})
