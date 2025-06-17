(ns soap-sync.utils)

(defn chunk-rows
  "Chunk a list into a matrix of n sized rows. This is useful for API calls that
  have a maximum item limit."
  [n xs]
  (lazy-seq
    (when (seq xs)
      (cons (take n xs)
            (chunk-rows n (drop n xs))))))

