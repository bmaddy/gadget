(ns gadget.core
  (:require [datomic.api :as d]
            [clojure.inspector :as i :refer [inspect-tree]])
  (:import (javax.swing.tree TreeModel)
           (javax.swing JTree JScrollPane JFrame)))

(defn rev-attrs
  "Finds reverse relationships for a datomic.query.EntityMap"
  [e]
  (let [attrs (d/q '[:find [?i ...]
                     :in $ ?target
                     :where
                     [_ ?a ?target]
                     [?a :db/ident ?i]]
                   (.db e) (:db/id e))]
    (map #(keyword (namespace %) (str \_ (name %))) attrs)))

(defn with-rev-attrs [e]
  "Makes datomic.query.EntityMaps include reverse references
   (first tier only to avoid an infinite loop)"
  (proxy [clojure.lang.Seqable] []
    (seq [] (concat (seq e)
                    (map (fn [k]
                           ;; not creating MapEntry directly here
                           ;; https://stackoverflow.com/questions/45151994/create-a-map-entry-in-clojure#answer-48364287
                           (first {k (k e)}))
                         (rev-attrs e))))))

(defn all-data
  "Adds reverse attributes if `data` is an EntityMap"
  [data]
  (if (instance? datomic.query.EntityMap data)
    (with-rev-attrs data)
    data))

(defn tree-model [data]
  (proxy [TreeModel] []
    (getRoot [] (all-data data))
    (addTreeModelListener [treeModelListener])
    (getChild [parent index]
      (i/get-child (all-data parent) index))
    (getChildCount [parent]
      (i/get-child-count (all-data parent)))
    (isLeaf [node]
      (i/is-leaf node))
    (valueForPathChanged [path newValue])
    (getIndexOfChild [parent child]
      -1)
    (removeTreeModelListener [treeModelListener])))

(defn inspect-entity-tree
  "creates a graphical (Swing) inspector on the supplied hierarchical data"
  [data]
  (doto (JFrame. "Clojure Inspector")
    (.add (JScrollPane. (JTree. (tree-model data))))
    (.setSize 400 600)
    (.setVisible true)))
