(ns soap-sync.xml-soap-test
  (:require [clojure.test :refer :all]
            [soap-sync.xml-soap :as xs]
            [clojure.data.xml :refer [element emit-str]]))

(deftest to-string-test []
  (testing "To string conversts xml objects to strings"
    (let [xml (element :test {:attr "value"} "inner")
          expected (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        "<test attr=\"value\">inner</test>")]
      (is (= (xs/to-string xml) expected)))))

(deftest to-xml-map-test []
  (testing "The parse-str wrapper works"
    (is (= (xs/to-xml-map "<div>test</div>") (element :div {} "test")))))

(deftest get-elements-by-tag-name-test []
  (testing "We can get XML elements by tag name:\n"
    (let [xml (element :body {}
                       (element :h1)
                       (element :h2 {} (element :div))
                       (element :div)
                       (element :div))]
      (testing "- We can get the H1 tag\n"
        (is (= (first (xs/get-elements-by-tag-name :h1 xml))
               (element :h1))))
      (testing "- We can get the nested divs"
        (is (= (xs/get-elements-by-tag-name :div xml)
               (:content (element :body {}
                                  (element :div)
                                  (element :div)
                                  (element :div)))))))))

(deftest create-account-detail-item-test []
  (testing "We get the expected detail item structure"
    (let [guid "xxxx-xxxx-xxxx-xxxx"
          expected (element :AccountDetailListRequestItem {}
                            (element :userGuid {} guid)
                            (element :accountTypeCode {} "Business"))]
      (is (= (xs/create-account-detail-item guid) expected)))))

(deftest create-account-detail-list-test []
  (testing "We get an account detail list with a list of guids"
    (let [guids '("xxxx-xxxx-xxxx-xxxx" "yyyy-yyyy-yyyy-yyyy")
          xmlns (System/getenv "ACTION_ROOT")
          expected (element :getAccountDetailList {:xmlns xmlns}
                            (element :accountDetailListRequest {}
                                     (element :onlineServiceId {}
                                              (System/getenv "SERVICE_ID"))
                                     (element :requesterAccountTypeCode {}
                                              "Internal")
                                     (element :requesterUserGuid {}
                                              (System/getenv "ACCOUNT_GUID"))
                                     (element :requestItemList {}
                                              (map xs/create-account-detail-item
                                                   guids))))]
      (is (= (xs/create-account-detail-list guids) expected)))))

(deftest create-soap-body-test []
  (testing "We wrap soap content in a soap body xml object"
    (let [content (element :test {} "inner")
          expected (element :soapenv:Body {} content)]
      (is (= (xs/create-soap-body content) expected)))))

(deftest create-soap-envelope-test []
  (testing "We get soap content wrapped in an envelope"
    (let [content (xs/create-soap-body (element :test {} "inner"))
          expected (element :soapenv:Envelope
                            {:xmlns:soapenv
                             "http://schemas.xmlsoap.org/soap/envelope/"}
                            (element :soapenv:Header)
                            content)]
      (is (= (xs/create-soap-envelope content) expected)))))
