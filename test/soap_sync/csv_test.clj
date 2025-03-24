(ns soap-sync.csv-test
  (:require [clojure.test :refer :all]
            [soap-sync.csv :as csv]
            [clojure.data.csv :as c]
            [clojure.java.io :as io]))

(def csv-data (let [data (with-open
                           [reader (io/reader (io/resource "input-test.csv"))]
                           (doall (c/read-csv reader)))]
                (map zipmap
                     (->> (first data)
                          repeat)
                     (rest data))))

(deftest get-input-data-test []
  (testing "We can get the input data from the source files:\n"
    (testing "- We can get the file from an environemnt variable\n"
      (is (= (csv/get-input-data) csv-data)))
    (testing "- We can get the file from a supplied argument"
      (is (= (csv/get-input-data "input-test.csv") csv-data)))))
