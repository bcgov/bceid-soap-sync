(ns soap-sync.core
  (:gen-class)
  (:require [clojure.core.async :refer [chan thread <!! >!! close!]]
            [soap-sync.csv :as csv]
            [soap-sync.xml-soap :as xs]
            [soap-sync.utils :refer [chunk-rows]]
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

(defn extract-csv-row [[detail-list-item [guid bceid]]]
  (let [individual (first (xs/get-elements-by-tag-name
                            :individualIdentity detail-list-item))]
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
     (->> detail-list-item (xs/get-element-by-tag-name :failureCode)
          :content first)
     bceid
     guid]))

(defn prepare-callout [chunk]
  (let [ids (map #(list (get % "GUID")
                        (get % "BCeID")) chunk)]
    (-> ids
        xs/create-account-detail-list
        xs/create-soap-body
        xs/create-soap-envelope
        xs/to-string
        (vector ids))))

(defn process-api-callouts [callouts]
  (let [channel (chan)]
    (thread (doseq [[envelope ids] callouts]
              (>!! channel (vector (send-soap-request "getAccountDetailList" envelope)
                                   ids)))
            (close! channel))
    (loop [responses []
           callout 0]
      (if-let [[res ids] (<!! channel)]
        (do
          (println "Received response from callout " callout)
          (println "API Response Code: " (:status res))
          (recur (if-not (= 200 (:status res))
                   responses
                   (conj responses (vector (:body res) ids)))
                 (inc callout)))
        responses))))

(defn get-detail-list-presponse-item [[response, ids]]
  (map vector
       (xs/get-elements-by-tag-name :AccountDetailListResponseItem
                                    (xs/to-xml-map response))
       ids))

(defn generate-output-data [response-body]
  (into [] (concat
             [["User ID" "GUID" "First Name" "Last Name" "Failure Code"
               "Source BCeID" "Source GUID"]]
             (map extract-csv-row response-body))))

(defn -main []
  (let [api-responses (->> (chunk-rows 100 (csv/get-input-data))
                           (map prepare-callout)
                           process-api-callouts
                           (mapcat get-detail-list-presponse-item))]
    (csv/write-output-data (generate-output-data api-responses))))
