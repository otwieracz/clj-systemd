(ns clj-systemd.manager-test
  (:require [clj-systemd.manager :as sut]
            [clj-systemd.systemd :as systemd]
            [clojure.test :refer [deftest is testing]]
            [me.raynes.fs :as fs]
            [clojure.java.io :as io]))

(def service
  "[Unit]
  Description=clj-systemd test service

  [Service]
  Type=simple
  ExecStart=/usr/bin/sleep 10

  [Install]
  WantedBy=multi-user.target")

(def service-name "clj-systemd-test.service")
(def service-path (str (System/getenv "HOME") "/.config/systemd/user/"))

(defn- create-test-service
  []
  (fs/mkdirs service-path)
  (with-open [w (io/writer (str service-path service-name) :append false)]
    (.write w service)))

(defn- delete-test-service
  []
  (fs/delete (str service-path service-name)))

(deftest start-unit-test
  (testing "test service unit"
    (let [manager (sut/get-manager (systemd/get-systemd :user))]
      (create-test-service)
      (testing "detection"
        (sut/reload manager)
        (Thread/sleep 500)
        (is (= 0 (:main-pid (sut/get-service manager service-name)))))
      (testing "starting"
        (sut/start-unit manager service-name :fail)
       ;; started
        (is (pos-int? (:main-pid (sut/get-service manager service-name)))))
       ;; after 10 seconds it should die, wait 11
      (Thread/sleep (* 11 1000))
      (testing "process exit detection"
        (is (= 0 (:main-pid (sut/get-service manager service-name)))))
      (delete-test-service)
      (systemd/disconnect :user))))
