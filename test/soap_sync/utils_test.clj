(ns soap-sync.utils-test
  (:require [clojure.test :refer :all]
            [soap-sync.utils :as utils]))

(deftest chunk-rows-test []
  (testing "Chunk rows makes a matrix of chunks"
    (let [list-of-things (range 0 10)
          expected-chunks [[0 1] [2 3] [4 5] [6 7] [8 9]]]
      (is (= (utils/chunk-rows 2 list-of-things) expected-chunks)))))
