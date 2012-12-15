(ns ns-dependencies.view
  (:import [javax.swing JFrame JPanel SwingUtilities]
           [java.awt Color RenderingHints]))
;;----------------------------------
(defn queue-action [f]
  (SwingUtilities/invokeLater f))
;;----------------------------------
(defrecord Figure [x y w h ns])
;;----------------------------------
(defn center [f]
  (vector (-> f (.w) (/ 2) (+ (.x f)) int)
          (-> f (.h) (/ 2) (+ (.y f)) int)))
;;----------------------------------
(defn draw-line [g f1 f2]
  (apply #(.drawLine g %1 %2 %3 %4) (concat (center f1) (center f2))))
;;----------------------------------
(defn paint-figures [g figs]
  (doseq [f (vals @figs)]
    (let [x   (.x f)
          y   (.y f)
          w   (.w f)
          h   (.h f)
          nsp (.ns f)
          s   (str (.name nsp))
          uses (.uses nsp)
          requires (.requires nsp)]
    (.setColor g Color/BLACK)
    (.drawOval g x y w h)
    (apply #(.drawString g s %1 %2) (center f))

    (.setColor g Color/BLUE)
    (doseq [u uses] (when (@figs u) (draw-line g f (@figs u))))

    (.setColor g Color/CYAN)
    (doseq [r requires] (when (@figs r) (draw-line g f (@figs r)))))))
;;----------------------------------
(defn build-panel [figs]
  (let [panel (proxy [JPanel] []
                (paintComponent [g]
                  (.setRenderingHint g RenderingHints/KEY_ANTIALIASING RenderingHints/VALUE_ANTIALIAS_ON)
                  (proxy-super paintComponent g)
                  (paint-figures g figs)))]
    (doto panel
      (.setBackground Color/LIGHT_GRAY))))
;;----------------------------------
(defn make-figure [ns n fpos fsize]
  (let [[x y] (fpos n)
        [w h] (fsize ns)]
    (Figure. x y w h ns)))
;;----------------------------------
(defn circle-pos [x y r n]
  (let [pi (* 2 Math/PI)
        dt (/ pi n)]
    (fn [i]
      [(->> i (* dt) Math/cos (* r) (+ x) int)
       (->> i (* dt) Math/sin (* r) (+ y) int)])))
;;----------------------------------
(defn weighted-size [mw mh dw dh]
  (fn [ns]
    (let [weight (+ (count (:uses ns)) (count (:requires ns)))
          x      (-> weight (* dw) (+ mw) int)]
      [x x])))
;;----------------------------------
(defn create-figures [nss mx my]
  (let [x0     (/ mx 2)
        y0     (/ my 2)
        r      (* (min x0 y0) 0.9)
        fpos   (circle-pos x0 y0 r (count nss))
        fsize  (weighted-size 10 10 10 10)]
    (->> (vals nss) 
         (mapcat #(vector (.name %2) 
                          (make-figure %2 %1 fpos fsize)) (range (count nss)))
         (apply assoc {}))))
;;----------------------------------
(defn view-deps [ns-deps w h]
  (let [figures  (atom (create-figures ns-deps w h))]
    (build-panel figures)))
;;----------------------------------