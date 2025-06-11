(ns soap-sync.xml-soap
  (:require [clojure.data.xml :refer [element emit-str parse-str]]))

(defn to-string [xml-object] (emit-str xml-object))
(defn to-xml-map [xml-string] (parse-str xml-string))

(defn get-elements-by-tag-name
  "Get all elements by tag name."
  [tag-name xml-map]
  (filter #(= (:tag %) tag-name)
          (tree-seq #(seq? (:content %)) #(:content %) xml-map)))

(defn get-element-by-tag-name
  "Get the first element by tag name."
  [tag-name xml-map]
  (first (get-elements-by-tag-name tag-name xml-map)))

(defn get-tag-value
  "Many objects have a nested 'value' tag that actually holds the tag value.
  This makes extracting that nested value feel less repetative."
  [xml-tag]
  (->> xml-tag :content first :content first))

(defn create-account-detail-item [ids]
  (let [[guid user-id] ids]
    (element :AccountDetailListRequestItem {}
             (cond (seq guid) (element :userGuid {} guid)
                   (seq user-id) (element :userId {} user-id)
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
