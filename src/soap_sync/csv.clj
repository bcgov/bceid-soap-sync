(ns soap-sync.csv
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(defn get-guid [row]
  (get row 1))

(defn get-input-data [& path]
  (let [input-file (or path (System/getenv "INPUT_CSV"))]
    (rest (with-open [reader (io/reader input-file)]
            (doall
             (csv/read-csv reader))))))
