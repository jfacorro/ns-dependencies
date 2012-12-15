(ns ns-dependencies.core
  (:require [ns-dependencies.load :as l]
            [ns-dependencies.view :as v]
            [clojure.pprint]))

(defn -main 
  ([] (-main "."))
  ([path]
    (println path)
    (let [ns-dep (l/load-project-ns path)
          w      500
          h      500
          pnl    (v/view-deps ns-dep w h)]
      (clojure.pprint/pprint ns-dep)
      (doto (javax.swing.JFrame.) 
        (.setDefaultCloseOperation javax.swing.JFrame/EXIT_ON_CLOSE)
        (.setSize w h)
        (.add pnl)
        (.setVisible true)))))
