(ns soap-sync.core
  (:gen-class)
  (:require [soap-sync.csv :as csv]
            [soap-sync.xml-soap :as xs]
            [soap-sync.bceid :as bceid]))

;;; Construct an API call with a soap envelope
;; An example:
;; (defn test-api-call []
;;   (bceid/send-soap-request "getAccountDetailList"
;;                            (-> (map csv/get-guid (csv/get-input-data))
;;                                xs/create-account-detail-list
;;                                xs/create-soap-body
;;                                xs/create-soap-envelope
;;                                xs/to-string)))

(defn -main [])
