# clj-systemd

[![Clojars Project](https://img.shields.io/clojars/v/clj-systemd.svg)](https://clojars.org/clj-systemd)

Basic interface to `systemd` in Clojure via dbus. For now only basic service and timer functions are implemented.

## Usage

### With `danielsz.system` or `com.stuartsierra.component`

```clojure
(:require (...)
          [clj-systemd.component :refer [new-systemd]
          [clj-systemd.manager :as manager]])

(defsystem my-system
           (...)
           :systemd (new-systemd :instance-type :system))

(manager/get-service (:systemd system) "my-service.service")
```

### Standalone

```clojure
(:require (...)
          [clj-systemd.systemd :as systemd]
          [clj-systemd.manager :as manager])
 
(let [systemd (systemd/get-systemd :system)
      manager (-> (systemd/get-systemd :system)
                  (manager/get-manager)]
  (...)
  ;; Clean up after, this will disconnect all :system managers
  ;; as `manager` is implemented as singleton in Java
  (manager/disconnect :system)
```

### Functions idefined on top of `manager`

* `get-service [manager service-name]` - Get Service `SERVICE-NAME`
* `get-unit [manager unit-name]` - Get Unit `UNIT-NAME`
* `get-timer [manager timer-name]` - Get Timer `TIMER-NAME`
* `start-unit - [manager unit start-mode]` - Start unit `UNIT-NAME` with mode `START-MODE`
* `stop-unit [manager unit stop-mode]` - Stop unit `UNIT-NAME` with mode `STOP-MODE`
* `restart-unit [manager unit restart-mode]` - Restart unit `UNIT-NAME` with mode `RESTART-MODE`
* `reload [manager]` - Reload systemd daemon

For more information about things like `start-mode`, etc see https://www.freedesktop.org/wiki/Software/systemd/dbus/

## Credits

This library is based on top of https://github.com/thjomnx/java-systemd

## License

Copyright © 2020 Slawomir Gonet <slawek@otwiera.cz>

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
