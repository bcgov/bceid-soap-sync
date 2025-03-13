(ns soap-sync.xml-soap
  (:require [clj-http.client :as client]
            [clojure.java.io :as io]
            [clojure.data.xml :refer [element emit-str parse-str]]
            [soap-sync.csv :as csv]))

(defn to-string [xml-object] (emit-str xml-object))
(defn to-xml-map [xml-string] (parse-str xml-string))

(defn get-elements-by-tag-name [tag-name xml-map]
  (filter #(= (:tag %) tag-name)
          (tree-seq #(seq? (:content %)) #(:content %) xml-map)))

(defn create-account-detail-item [guid]
  (element :AccountDetailListRequestItem {}
           (element :userGuid {} guid)
           (element :accountTypeCode {} "Business")))

(defn create-account-detail-list [guids]
  (let [xmlns (System/getenv "ACTION_ROOT")]
    (element :getAccountDetailList {:xmlns xmlns}
             (element :accountDetailListRequest {}
                      (element :onlineServiceId {}
                               (System/getenv "SERVICE_ID"))
                      (element :requesterAccountTypeCode {} "Internal")
                      (element :requesterUserGuid {}
                               (System/getenv "ACCOUNT_GUID"))
                      (element :requestItemList {}
                               (map create-account-detail-item guids))))))

(defn create-soap-body [body-content]
  (element :soapenv:Body {} body-content))

(defn create-soap-envelope [body]
  (element :soapenv:Envelope {:xmlns:soapenv
                              "http://schemas.xmlsoap.org/soap/envelope/"}
           (element :soapenv:Header)
           body))
