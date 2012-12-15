(ns ns-dependencies.load
  (:import [java.io File])
  (:require [clojure.zip :as z]))
;;------------------------------
(defrecord Namespace
  [name file uses requires])
;;------------------------------
(defprotocol FileNode
  (file? [this])
  (directory? [this])
  (filename [this])
  (rel-path [this])
  (abs-path [this]))
;;------------------------------
(extend-type java.io.File 
  FileNode
  (file? [this] (-> this directory? not))
  (directory? [this] (.isDirectory this))
  (filename [this] (.getName this))
  (rel-path [this] (.getPath this))
  (abs-path [this] (.getPath this)))
;;------------------------------
(defn list-all-files
  "Explores the given path recursevily 
  obtaining a list of all *.clj files."
  [s]
  (let [file (File. s)
        children (.listFiles file)
        children (filter #(or (directory? %) (re-matches #".*\.clj" (filename %))) children)]
    (mapcat #(if (directory? %) (list-all-files (rel-path %)) [%]) children)))
;;------------------------------
(defn extract-ns [lst]
  (->> lst first rest (map second) (map #(if (sequential? %) (first %) %))))
;;------------------------------
(defn find-node [zp x]
  "Explores the zipper using next until it finds
  x or the end is reached."
  (cond (z/end? zp) nil
        (and (seq? (first zp)) (= (ffirst zp) x)) zp
        (= (first zp) x) zp
        :else (recur (z/next zp) x)))
;;------------------------------
(defn ns-from-file [file]
  "Looks for the ns declaration form in the file and
  creares a Namespace record with the :uses and :requires
  for the given ns"
  (let [contents (slurp (abs-path file))
        form     (when-not (empty? contents) (read-string contents))]
    (when (and form (= (first form) 'ns))
      (let [name-ns  (second form)
            expanded (when (= (first form) 'ns) (macroexpand form))
            zp       (z/zipper coll? rest #(-> %2) expanded)
            uses     (-> (find-node zp 'clojure.core/use) extract-ns)
            requires (-> (find-node zp 'clojure.core/require) extract-ns)]
      (Namespace. name-ns file uses requires)))))
;;------------------------------
(defn load-project-ns [path]
  "Looks for all the *.clj files in path recursively
  and resturn a map of Namespaces containing dependency 
  information."
  (let [files   (list-all-files path)
        nss-seq (filter #(-> %)(map ns-from-file files))
        nss-map (reduce conj {} (map #(vector (.name %) %) nss-seq))]
    nss-map))
;;------------------------------
#_(
  ;; trying out the loading
  (def p "C:\\Juan\\Dropbox\\Facultad\\2012.Trabajo.Profesional\\ide\\src")
  (clojure.pprint/pprint (load-project-ns p))
)