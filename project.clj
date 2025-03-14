(defproject bceid-soap-sync "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/data.csv "1.1.0"]
                 [org.clojure/data.xml "0.0.8"]
                 [clj-http "3.13.0"]
                 [org.apache.logging.log4j/log4j-api "2.11.0"]
                 [org.apache.logging.log4j/log4j-core "2.11.0"]
                 [org.apache.logging.log4j/log4j-1.2-api "2.11.0"]]
  :plugins [[lein-cloverage "1.2.2"]]
  :cloverage {:html? false}
  :main ^:skip-aot soap-sync.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
