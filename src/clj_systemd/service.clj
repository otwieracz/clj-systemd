(ns clj-systemd.service
  (:import (de.thjom.java.systemd Service)
           (de.thjom.java.systemd.types EnvironmentFile))
  (:require [clojure.spec.alpha :as spec]
            [clojure.string :as string]
            [clj-systemd.unit :as unit]))

(spec/def ::pid (spec/and int? #(not (neg-int? %))))
(spec/def ::main-pid ::pid)
(spec/def ::bus-name string?)
;; CAPABILITY_BOUNDING_SET = "CapabilityBoundingSet";
(spec/def ::control-pid ::pid)
;; DELEGATE = "Delegate";
;; DELEGATE_CONTROLLERS = "DelegateControllers";
;; DEVICE_ALLOW = "DeviceAllow";
;; DEVICE_POLICY = "DevicePolicy";
(spec/def ::environment map?)
(spec/def ::file-path string?)
(spec/def ::prefixed boolean?)
(spec/def ::environment-file (spec/keys :req-un [::file-path ::prefixed]))
(spec/def ::environment-files (spec/coll-of ::environment-file))
;; EXEC_MAIN_CODE = "ExecMainCode";
;; EXEC_MAIN_EXIT_TIMESTAMP = "ExecMainExitTimestamp";
;; EXEC_MAIN_EXIT_TIMESTAMP_MONOTONIC = "ExecMainExitTimestampMonotonic";
;; EXEC_MAIN_PID = "ExecMainPID";
;; EXEC_MAIN_START_TIMESTAMP = "ExecMainStartTimestamp";
;; EXEC_MAIN_START_TIMESTAMP_MONOTONIC = "ExecMainStartTimestampMonotonic";
(spec/def ::exec-main-status int?)
;; EXEC_RELOAD = "ExecReload";
;; EXEC_START = "ExecStart";
;; EXEC_START_POST = "ExecStartPost";
;; EXEC_START_PRE = "ExecStartPre";
;; EXEC_STOP = "ExecStop";
;; EXEC_STOP_POST = "ExecStopPost";
;; FILE_DESCRIPTOR_STORE_MAX = "FileDescriptorStoreMax";
;; GROUP = "Group";
;; GUESS_MAIN_PID = "GuessMainPID";
;; IO_SCHEDULING_CLASS = "IOSchedulingClass";
;; IO_SCHEDULING_PRIORITY = "IOSchedulingPriority";
;; IGNORE_SIGPIPE = "IgnoreSIGPIPE";
;; INACCESSIBLE_PATHS = "InaccessiblePaths";
;; KILL_MODE = "KillMode";
;; KILL_SIGNAL = "KillSignal";
;; MOUNT_FLAGS = "MountFlags";
;; NFILE_DESCRIPTOR_STORE = "NFileDescriptorStore";
;; NICE = "Nice";
;; NO_NEW_PRIVILEGES = "NoNewPrivileges";
;; NON_BLOCKING = "NonBlocking";
;; NOTIFY_ACCESS = "NotifyAccess";
;; N_RESTARTS = "NRestarts";
;; OOM_SCORE_ADJUST = "OOMScoreAdjust";
;; PAM_NAME = "PAMName";
;; PID_FILE = "PIDFile";
;; PERMISSIONS_START_ONLY = "PermissionsStartOnly";
;; READ_ONLY_PATHS = "ReadOnlyPaths";
;; READ_WRITE_PATHS = "ReadWritePaths";
;; REMAIN_AFTER_EXIT = "RemainAfterExit";
;; RESTART = "Restart";
;; RESTART_FORCE_EXIT_STATUS = "RestartForceExitStatus";
;; RESTART_PREVENT_EXIT_STATUS = "RestartPreventExitStatus";
;; RESTART_USEC = "RestartUSec";
;; RESULT = "Result";
;; ROOT_DIRECTORY = "RootDirectory";
;; ROOT_DIRECTORY_START_ONLY = "RootDirectoryStartOnly";
;; RUNTIME_MAX_USEC = "RuntimeMaxUSec";
;; SAME_PROCESS_GROUP = "SameProcessGroup";
;; SECURE_BITS = "SecureBits";
;; SEND_SIGHUP = "SendSIGHUP";
;; SEND_SIGKILL = "SendSIGKILL";
;; SLICE = "Slice";
;; STATUS_ERRNO = "StatusErrno";
;; STATUS_TEXT = "StatusText";
;; SUCCESS_EXIT_STATUS = "SuccessExitStatus";
;; SUPPLEMENTARY_GROUPS = "SupplementaryGroups";
;; SYSLOG_IDENTIFIER = "SyslogIdentifier";
;; SYSLOG_LEVEL_PREFIX = "SyslogLevelPrefix";
;; SYSLOG_PRIORITY = "SyslogPriority";
;; SYSTEM_CALL_FILTER = "SystemCallFilter";
;; TTY_PATH = "TTYPath";
;; TTY_RESET = "TTYReset";
;; TTY_V_HANGUP = "TTYVHangup";
;; TTY_VT_DISALLOCATE = "TTYVTDisallocate";
;; TASKS_ACCOUNTING = "TasksAccounting";
;; TASKS_CURRENT = "TasksCurrent";
;; TASKS_MAX = "TasksMax";
;; TIMEOUT_START_USEC = "TimeoutStartUSec";
;; TIMEOUT_STOP_USEC = "TimeoutStopUSec";
;; TIMER_SLACK_NSEC = "TimerSlackNSec";
;; TYPE = "Type";
;; UMASK = "UMask";
;; USB_FUNCTION_DESCRIPTORS = "USBFunctionDescriptors";
;; USB_FUNCTION_STRINGS = "USBFunctionStrings";
;; WATCHDOG_TIMESTAMP = "WatchdogTimestamp";
;; WATCHDOG_TIMESTAMP_MONOTONIC = "WatchdogTimestampMonotonic";
;; WATCHDOG_USEC = "WatchdogUSec";
;; WORKING_DIRECTORY = "WorkingDirectory";

(spec/def ::service (spec/merge ::unit/unit
                                (spec/keys :req-un [::main-pid ::environment ::environment-files ::exec-main-status])))

(spec/def ::service-instance #(= (type %) de.thjom.java.systemd.Service))

(defn- get-environment
  [service-instance]
  {:pre [(spec/valid? ::service-instance service-instance)]}
  (apply hash-map
         (reduce concat
                 (map #(string/split % #"=") (.getEnvironment service-instance)))))

(defn- get-environment-files
  [service-instance]
  {:pre [(spec/valid? ::service-instance service-instance)]}
  (map (fn [ef]
         {:file-path (.getFilePath ef)
          :is-prefixed (.isPrefixed ef)})
       (.getEnvironmentFiles service-instance)))

(defn to-service
  "Convert `SERVICE-INSTANCE` to hash-map"
  [service-instance]
  {:pre [(spec/valid? ::service-instance service-instance)]
   :post [(spec/valid? ::service %)]}
  (merge (unit/to-unit service-instance)
         {:main-pid (.getMainPID service-instance)
          :environment (get-environment service-instance)
          :environment-files (get-environment-files service-instance)
          :exec-main-status (.getExecMainStatus service-instance)}))


