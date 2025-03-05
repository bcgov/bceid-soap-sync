(ns soap-sync.csv-test
  (:require [clojure.test :refer :all]
            [soap-sync.csv :as csv]
            [clojure.data.csv :as c]
            [clojure.java.io :as io]))

(deftest get-guid-test []
  (testing "We get the GUID from a CSV row"
    (let [csv-data (rest (with-open
                           [reader (io/reader "test/data/input-test.csv")]
                           (doall (c/read-csv reader))))]
      (is (= (csv/get-guid (first csv-data)) "asdf-asdf-asdf-asdf")))))

