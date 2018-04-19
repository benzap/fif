(defproject fif "0.3.0-SNAPSHOT"
  :description "Stack-based Programming in Clojure"
  :url "http://github.com/benzap/fif"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.238"]
                 [org.clojure/tools.reader "1.2.1"]]
  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-ancient "0.6.15"]]
  ;;:hooks [leiningen.cljsbuild]
  :repositories [["clojars" {:sign-releases true}]]
  :cljsbuild {:builds {:dev
                       {:source-paths ["src"]
                        :compiler {:output-dir "resources/public/js/compiled/out"
                                   :output-to "resources/public/js/compiled/fif.js"
                                   :optimizations :whitespace
                                   :pretty-print true
                                   :source-map "resources/public/js/compiled/fif.js.map"}}
                       :prod
                       {:source-paths ["src"]
                        :compiler {:output-to "resources/public/js/compiled/fif.min.js"
                                   :optimizations :advanced
                                   :pretty-print false}}}})
