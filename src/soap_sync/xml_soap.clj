(ns soap-sync.xml-soap
  (:require [clj-http.client :as client]
            [clojure.java.io :as io]
            [clojure.data.xml :refer [element emit-str parse-str]]
            [soap-sync.csv :as csv]))

(defn to-string [xml-object] (emit-str xml-object))
(defn to-xml-map [xml-string] (parse-str xml-string))

(defn get-elements-by-tag-name [tag-name xml-map]
  "Get all elements by tag name."
  (filter #(= (:tag %) tag-name)
          (tree-seq #(seq? (:content %)) #(:content %) xml-map)))

(defn get-element-by-tag-name [tag-name xml-map]
  "Get the first element by tag name."
  (first (get-elements-by-tag-name tag-name xml-map)))

(defn get-tag-value [xml-tag]
  "Many objects have a nested 'value' tag that actually holds the tag value.
This makes extracting that nested value feel less repetative."
  (->> xml-tag :content first :content first))

(defn create-account-detail-item [ids]
  (let [[guid user-id] ids]
    (element :AccountDetailListRequestItem {}
             (cond (not (empty? guid)) (element :userGuid {} guid)
                   (not (empty? user-id)) (element :userId {} user-id)
                   :else (throw (Throwable. "GUID or userId must be supplied")))
             (element :accountTypeCode {} "Business"))))

(defn create-account-detail-list [ids]
  (let [xmlns (System/getenv "ACTION_ROOT")]
    (element :getAccountDetailList {:xmlns xmlns}
             (element :accountDetailListRequest {}
                      (element :onlineServiceId {}
                               (System/getenv "SERVICE_ID"))
                      (element :requesterAccountTypeCode {} "Internal")
                      (element :requesterUserGuid {}
                               (System/getenv "ACCOUNT_GUID"))
                      (element :requestItemList {}
                               (map create-account-detail-item ids))))))

(defn create-soap-body [body-content]
  (element :soapenv:Body {} body-content))

(defn create-soap-envelope [body]
  (element :soapenv:Envelope {:xmlns:soapenv
                              "http://schemas.xmlsoap.org/soap/envelope/"}
           (element :soapenv:Header)
           body))
