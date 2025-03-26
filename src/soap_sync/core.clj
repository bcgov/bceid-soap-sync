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

(defn -main [])
