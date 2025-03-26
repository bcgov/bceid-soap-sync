(ns soap-sync.core
  (:gen-class)
  (:require [soap-sync.csv :as csv]
            [soap-sync.xml-soap :as xs]
            [clj-http.client :as client]))

(defn send-soap-request [action soap-envelope]
  (let [url (str (System/getenv "SERVICE_SITE")
                 "/webservices/client/V10/BCeIDService.asmx?WSDL")
        username (System/getenv "ACCOUNT_NAME")
        password (System/getenv "ACCOUNT_PASSWORD")
        headers {"SOAPAction"
                 (str (System/getenv "ACTION_ROOT") action)}]
    (client/post url
                 {:basic-auth [username password]
                  :headers headers
                  :body soap-envelope
                  :content-type "text/xml;charset=utf-8"
                  :throw-exceptions false})))

(defn extract-csv-row [detail-list-item]
  (let [individual (first (xs/get-elements-by-tag-name
                           :individualIdentity detail-list-item))
        business (first (xs/get-elements-by-tag-name
                         :business detail-list-item))]
    [(->> detail-list-item
          (xs/get-element-by-tag-name :userId)
          xs/get-tag-value)
     (->> detail-list-item
          (xs/get-element-by-tag-name :guid)
          xs/get-tag-value)
     (->> individual
          (xs/get-element-by-tag-name :firstname)
          xs/get-tag-value)
     (->> individual
          (xs/get-element-by-tag-name :surname)
          xs/get-tag-value)
     (->> business
          (xs/get-element-by-tag-name :legalName)
          xs/get-tag-value)
     (->> business
          (xs/get-element-by-tag-name :businessNumber)
          xs/get-tag-value)
     (->> business
          (xs/get-element-by-tag-name :businessNumberVerifiedFlag)
          xs/get-tag-value)
     (->> business
          (xs/get-element-by-tag-name :statementOfRegistrationNumber)
          xs/get-tag-value)
     (->> business
          (xs/get-element-by-tag-name :incorporationNumber)
          xs/get-tag-value)
     (->> detail-list-item (xs/get-element-by-tag-name :failureCode)
          :content first)]))

(defn generate-output-data [response-body]
  (into [] (concat
            [["User ID" "GUID" "First Name" "Last Name" "Legal Business Name"
              "Business Number" "Business Number Verified"
              "Statement of Registration Number" "Incorporation Number"
              "Failure Code"]]
            (map extract-csv-row
                 (xs/get-elements-by-tag-name :AccountDetailListResponseItem
                                              (xs/to-xml-map response-body))))))

(defn -main []
  (let [api-response (send-soap-request "getAccountDetailList"
                                        (-> (map #(list (get % "GUID")
                                                        (get % "BCeID"))
                                                 (csv/get-input-data))
                                            xs/create-account-detail-list
                                            xs/create-soap-body
                                            xs/create-soap-envelope
                                            xs/to-string))]
    (when (not (= 200 (:status api-response)))
      (throw (Throwable. "API response was not OK.")))
    (csv/write-output-data (generate-output-data (:body api-response)))))
