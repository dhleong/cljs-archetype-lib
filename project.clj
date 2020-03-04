(defproject net.dhleong/archetype "0.1.0-SNAPSHOT"
  :description "Supporting library for archetype-based apps"
  :url "https://github.com/dhleong/cljs-archetype-lib"

  ;; this is optional, add what you want or remove it
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies
  ;; always use "provided" for Clojure(Script)
  [[org.clojure/clojurescript "1.10.520" :scope "provided"]
   ; navigation
   [clj-commons/secretary "1.2.4"]
   [kibu/pushy "0.3.8"]]

  :source-paths
  ["src/main"])
