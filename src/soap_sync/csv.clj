(ns soap-sync.csv
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(defn get-input-data [& resource-path]
  (let [input-file (or (when resource-path
                         (or (io/resource (first resource-path))
                             (first resource-path)))
                       (System/getenv "INPUT_CSV"))
        data (with-open [reader (io/reader input-file)]
               (doall
                (csv/read-csv reader)))]
    (map zipmap
         (->> (first data)
              repeat)
         (rest data))))
