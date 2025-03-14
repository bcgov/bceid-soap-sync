(ns soap-sync.csv-test
  (:require [clojure.test :refer :all]
            [soap-sync.csv :as csv]
            [clojure.data.csv :as c]
            [clojure.java.io :as io]))

(def csv-data (rest (with-open
                      [reader (io/reader "test/data/input-test.csv")]
                      (doall (c/read-csv reader)))))

(deftest get-guid-test []
  (testing "We get the GUID from a CSV row"
    (is (= (csv/get-guid (first csv-data)) "asdf-asdf-asdf-asdf"))))

(deftest get-userid-test []
  (testing "We can get the user id from a CSV row"
    (is (= (csv/get-userid (first csv-data)) "user-one"))))

(defn get-input-data-test []
  (testing "We can get the input data from the source files:\n"
    (testing "- We can get the file from an environemnt variable\n"
      (is (= (csv/get-input-data) csv-data)))
    (testing "- We can get the file from a supplied argument"
      (is (= (csv/get-input-data "test/data/input-test.csv") csv-data)))))
