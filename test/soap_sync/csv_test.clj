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
  (testing "- We can get the file from an environemnt variable"
    (is (= (csv/get-input-data) csv-data)))
  (testing "- We can get the file from a supplied argument"
    (is (= (csv/get-input-data "input-test.csv") csv-data))))

(deftest write-output-data-test []
  (let [csv-data [["Column 1" "Column 2"]
                  ["foo" "bar"]
                  ["baz" "whatever"]]
        expected-file "Column 1,Column 2\nfoo,bar\nbaz,whatever\n"
        tmp-file (java.io.File/createTempFile "out" ".csv")]
    (testing "We can write a csv with the default output file."
      (csv/write-output-data csv-data)
      (is (= (slurp "out.csv") expected-file))
      (io/delete-file "out.csv"))
    (testing "We can write a csv with a custom path"
      (csv/write-output-data csv-data tmp-file)
      (is (= (slurp (str (.getAbsolutePath tmp-file))) expected-file))
      (.delete tmp-file))))
