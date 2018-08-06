(ns clj-systemd.unit
  (:import (de.thjom.java.systemd Unit))
  (:require [clojure.spec.alpha :as spec]))

;; ACTIVE_ENTER_TIMESTAMP = "ActiveEnterTimestamp";
;; ACTIVE_ENTER_TIMESTAMP_MONOTONIC = "ActiveEnterTimestampMonotonic";
;; ACTIVE_EXIT_TIMESTAMP = "ActiveExitTimestamp";
;; ACTIVE_EXIT_TIMESTAMP_MONOTONIC = "ActiveExitTimestampMonotonic";
;; ACTIVE_STATE = "ActiveState";
(spec/def ::active-state #{:active :reloading :inactive :failed :activating :deactivating})
;; AFTER = "After";
;; ALLOW_ISOLATE = "AllowIsolate";
;; ASSERT_RESULT = "AssertResult";
;; ASSERT_TIMESTAMP = "AssertTimestamp";
;; ASSERT_TIMESTAMP_MONOTONIC = "AssertTimestampMonotonic";
;; ASSERTS = "Asserts";
;; BEFORE = "Before";
;; BINDS_TO = "BindsTo";
;; BOUND_BY = "BoundBy";
;; CAN_ISOLATE = "CanIsolate";
;; CAN_RELOAD = "CanReload";
;; CAN_START = "CanStart";
;; CAN_STOP = "CanStop";
;; COLLECT_MODE = "CollectMode";
;; CONDITION_RESULT = "ConditionResult";
;; CONDITION_TIMESTAMP = "ConditionTimestamp";
;; CONDITION_TIMESTAMP_MONOTONIC = "ConditionTimestampMonotonic";
;; CONDITIONS = "Conditions";
;; CONFLICTED_BY = "ConflictedBy";
;; CONFLICTS = "Conflicts";
;; CONSISTS_OF = "ConsistsOf";
;; DEFAULT_DEPENDENCIES = "DefaultDependencies";
;; DESCRIPTION = "Description";
;; DOCUMENTATION = "Documentation";
;; DROP_IN_PATHS = "DropInPaths";
;; FAILURE_ACTION = "FailureAction";
;; FOLLOWING = "Following";
;; FRAGMENT_PATH = "FragmentPath";
(spec/def ::id string?)
;; IGNORE_ON_ISOLATE = "IgnoreOnIsolate";
;; INACTIVE_ENTER_TIMESTAMP = "InactiveEnterTimestamp";
;; INACTIVE_ENTER_TIMESTAMP_MONOTONIC = "InactiveEnterTimestampMonotonic";
;; INACTIVE_EXIT_TIMESTAMP = "InactiveExitTimestamp";
;; INACTIVE_EXIT_TIMESTAMP_MONOTONIC = "InactiveExitTimestampMonotonic";
;; INVOCATION_ID = "InvocationID";
;; JOB = "Job";
;; JOB_RUNNING_TIMEOUT_USEC = "JobRunningTimeoutUSec";
;; JOB_TIMEOUT_ACTION = "JobTimeoutAction";
;; JOB_TIMEOUT_REBOOT_ARGUMENT = "JobTimeoutRebootArgument";
;; JOB_TIMEOUT_USEC = "JobTimeoutUSec";
;; JOINS_NAMESPACE_OF = "JoinsNamespaceOf";
;; LOAD_ERROR = "LoadError";
(spec/def ::load-state #{:loaded :error :masked})
;; NAMES = "Names";
;; NEED_DAEMON_RELOAD = "NeedDaemonReload";
;; ON_FAILURE = "OnFailure";
;; ON_FAILURE_JOB_MODE = "OnFailureJobMode";
;; PART_OF = "PartOf";
;; PERPETUAL = "Perpetual";
;; PROPAGATES_RELOAD_TO = "PropagatesReloadTo";
;; REBOOT_ARGUMENT = "RebootArgument";
;; REFUSE_MANUAL_START = "RefuseManualStart";
;; REFUSE_MANUAL_STOP = "RefuseManualStop";
;; RELOAD_PROPAGATED_FROM = "ReloadPropagatedFrom";
;; REQUIRED_BY = "RequiredBy";
;; REQUIRES = "Requires";
;; REQUIRES_MOUNTS_FOR = "RequiresMountsFor";
;; REQUISITE = "Requisite";
;; REQUISITE_OF = "RequisiteOf";
;; SOURCE_PATH = "SourcePath";
;; START_LIMIT_ACTION = "StartLimitAction";
;; START_LIMIT_BURST = "StartLimitBurst";
;; START_LIMIT_INTERVAL_USEC = "StartLimitIntervalUSec";
;; STATE_CHANGE_TIMESTAMP = "StateChangeTimestamp";
;; STATE_CHANGE_TIMESTAMP_MONOTONIC = "StateChangeTimestampMonotonic";
;; STOP_WHEN_UNNEEDED = "StopWhenUnneeded";
;; SUB_STATE = "SubState";
;; SUCCESS_ACTION = "SuccessAction";
;; TRANSIENT = "Transient";
;; TRIGGERED_BY = "TriggeredBy";
;; TRIGGERS = "Triggers";
;; UNIT_FILE_PRESET = "UnitFilePreset";
;; UNIT_FILE_STATE = "UnitFileState";
;; WANTED_BY = "WantedBy";
;; WANTS = "Wants";

(spec/def ::unit (spec/keys :req-un [::id ::active-state ::load-state]))

(spec/def ::unit-instance (spec/or :unit #(= (type %) de.thjom.java.systemd.Unit)
                                   :service #(= (type %) de.thjom.java.systemd.Service)
                                   :timer #(= (type %) de.thjom.java.systemd.Timer)))

(defn to-unit
  "Convert `UNIT-INSTANCE` to hash-map"
  [unit-instance]
  {:pre [(spec/valid? ::unit-instance unit-instance)]
   :post [(spec/valid? ::unit %)]}
  {:active-state (keyword (.getActiveState unit-instance))
   :load-state (keyword (.getLoadState unit-instance))
   :id (.getId unit-instance)})
