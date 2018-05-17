(defproject gadget "0.1.1"
  :description "A modified version of inspect-tree to work with Datomic entities."
  :url "https://github.com/bmaddy/gadget"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]]

  :profiles
  {:dev {:dependencies [[com.datomic/datomic-free "0.9.5561"]
                        [org.clojure/clojure "1.8.0"]]}})
