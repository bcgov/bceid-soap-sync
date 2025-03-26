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
  (let [xml (element :body {}
                     (element :h1)
                     (element :h2 {} (element :div))
                     (element :div)
                     (element :div))]
    (testing "We can get the H1 tag"
      (is (= (first (xs/get-elements-by-tag-name :h1 xml))
             (element :h1))))
    (testing "We can get the nested divs"
      (is (= (xs/get-elements-by-tag-name :div xml)
             (seq [(element :div)
                   (element :div)
                   (element :div)]))))))

(deftest get-element-by-tag-name-test []
  (testing "We can get the first element in a nested tree."
    (let [xml (element :body {}
                       (element :h2)
                       (element :h2 {} (element :h1 {:name "one"}))
                       (element :div)
                       (element :h2))]
      (is (= (xs/get-element-by-tag-name :h1 xml)
             (element :h1 {:name "one"}))))))

(deftest get-tag-value-test []
  (testing "We can get the innerText of a nested value tag."
    (let [xml (element :someThing {} (element :value {} "innerText"))]
      (is (= (xs/get-tag-value xml) "innerText")))))

(deftest create-account-detail-item-test []
  (testing "We get the expected detail item structure"
    (let [guid "xxxx-xxxx-xxxx-xxxx"
          expected (element :AccountDetailListRequestItem {}
                            (element :userGuid {} guid)
                            (element :accountTypeCode {} "Business"))]
      (is (= (xs/create-account-detail-item (list guid "userOne")) expected)))))

(deftest create-account-detail-list-test []
  (testing "We get an account detail list with a list of guids"
    (let [ids '(("xxxx-xxxx-xxxx-xxxx" "userOne")
                ("yyyy-yyyy-yyyy-yyyy" "userTwo"))
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
                                                   ids))))]
      (is (= (xs/create-account-detail-list ids) expected)))))

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
