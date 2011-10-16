(ns org.branimirkostic.supermarketsimulation.gui)


(import '(javax.swing JFrame JPanel JLabel JTextArea JTextField JButton JScrollPane)
        '(java.awt.event ActionListener)
        '(java.awt GridLayout BorderLayout)
        '(java.awt Color))

;----------------------Form Components------------------------------

(def open-button (JButton. "Open"))
(def close-button (JButton. "Close"))
(def dispose-button (JButton. "Dispose"))
(def customers-label (JLabel. "Customers:  0"))
(def entrance-label (JLabel. "  Entrance1:   "))
(def entrance-label2 (JLabel. "  Entrance2:   "))
(def entrance-label3 (JLabel. "  Entrance3:   "))
(def register-label (JLabel. " Register1: "))
(def register-label2 (JLabel. " Register2: "))
(def register-label3 (JLabel. " Register3: "))
(def register-label4 (JLabel. " Register4: "))
(def register-label5 (JLabel. " Register5: "))
(def register-textarea (JTextArea.))
(.setEditable register-textarea false)
(def register-textarea2 (JTextArea.))
(.setEditable register-textarea2 false)
(def register-textarea3 (JTextArea.))
(.setEditable register-textarea3 false)
(def register-textarea4 (JTextArea.))
(.setEditable register-textarea4 false)
(def register-textarea5 (JTextArea.))
(.setEditable register-textarea5 false)
(def register-textarea-scroll (JScrollPane. register-textarea 20 30))
(def register-textarea-scroll2 (JScrollPane. register-textarea2 20 30))
(def register-textarea-scroll3 (JScrollPane. register-textarea3 20 30))
(def register-textarea-scroll4 (JScrollPane. register-textarea4 20 30))
(def register-textarea-scroll5 (JScrollPane. register-textarea5 20 30))

(def regs-panel (doto(JPanel.)
  (.setLayout (GridLayout. 1 5))
  (.add register-label)
  (.add register-label2)
  (.add register-label3)
  (.add register-label4)
  (.add register-label5)))

(def rows-panel (doto (JPanel.)
  (.setLayout (GridLayout. 1 5))
  (.add register-textarea-scroll)
  (.add register-textarea-scroll2)
  (.add register-textarea-scroll3)
  (.add register-textarea-scroll4)
  (.add register-textarea-scroll5)))

(def north-panel (doto (JPanel.)
  (.add open-button)
  (.add close-button)
  (.add dispose-button)
  (.add customers-label)))
(def west-panel (doto (JPanel.)
  (.setLayout (GridLayout. 0 1))
  (.add entrance-label)
  (.add entrance-label2)
  (.add entrance-label3)))

(def center-panel (doto (JPanel.)
  (.setLayout (BorderLayout.))
  (.add regs-panel "North")
  (.add rows-panel "Center")))

(def frame (JFrame. "Supermarket"))
  (.add frame north-panel "North")
  (.add frame west-panel "West")
  (.add frame center-panel "Center")
  (.setBounds frame 200 200 500 200)
  (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
  (.setEnabled close-button false)

;----------------------Functions---------------------------------

;number of customers in the supermarket
(def customer-no (ref 0))
;supermarket is working
(def works (ref false))
;max time between two entrances
(def max-entrance-time 15000)
;max shopping time
(def max-shopping-time 40000)

;row for every register
(def register-row-1 (ref []))
(def register-row-2 (ref []))
(def register-row-3 (ref []))
(def register-row-4 (ref []))
(def register-row-5 (ref []))

;customer
(defstruct customer :id :max-shopping-time)

(defn customer-in "Increasing the number of customers" [] (dosync(ref-set customer-no (inc @customer-no))))

(defn customer-out "Decreasing the number of customers" [] (dosync(ref-set customer-no (dec @customer-no))))

(defn random-entrance "Returns the entrance entered by customer" [random-no]
  (cond
    (= random-no 0) entrance-label
    (= random-no 1) entrance-label2
    (= random-no 2) entrance-label3))

(defn random-row "Returns a row entered by customer" [random-no]
  (cond
    (= random-no 0) [register-textarea register-row-1]
    (= random-no 1) [register-textarea2 register-row-2]
    (= random-no 2) [register-textarea3 register-row-3]
    (= random-no 3) [register-textarea4 register-row-4]
    (= random-no 4) [register-textarea5 register-row-5]))

(defn go-to-register [customer row]
	((Thread/sleep (customer :max-shopping-time))
   (.setText (get (random-row row) 0) (str (.getText (get (random-row row) 0)) "\n" (customer :id)))
   (dosync(ref-set (get (random-row row) 1) (conj @(get (random-row row) 1) (customer :id))))
   (println (get (random-row row) 1))
   (println (str "Row entry" customer))))

(defn run-entrance "creating a customer who enters the supermarket" []
  (loop [i 0]
    (when (= @works true)
      (def entrance-x (rand-int 3))
      (Thread/sleep (rand max-entrance-time))
      (customer-in)
      (def customer (agent {:id @customer-no :max-shopping-time (rand max-shopping-time)}))
      (pvalues (go-to-register @customer (rand-int 5)))
      (.setText customers-label (str "Customers: " @customer-no))
      (.setText (random-entrance entrance-x) (str "  Entrance" (inc entrance-x) ": " @customer-no " "))
      (println "Supermarket entry" @customer)
      (recur i))))

(defn open []
  (ref-set works true))
(defn close []
  (ref-set works false))

(defn set-color [color]
  ((.setBackground west-panel color)
    (.setBackground north-panel color)
    (.setBackground regs-panel color)
    (.setBackground rows-panel color)))

;----------------------Action Listeners------------------------------

(.addActionListener open-button
      (proxy [ActionListener] []
        (actionPerformed [evt]
                         (dosync(open))
                         (pcalls run-entrance)
                         ;(.start (Thread. #(run-entrance)))
                         (.setEnabled open-button false)
                         (.setEnabled close-button true)
                         ;(set-color Color/GREEN)
                         )))
(.addActionListener close-button
      (proxy [ActionListener] []
        (actionPerformed [evt]
          (dosync(close))
          (.setEnabled open-button true)
          (.setEnabled close-button false)
          ;(set-color Color/RED)
          )))
(.addActionListener dispose-button
      (proxy [ActionListener] []
        (actionPerformed [evt]
            (System/exit 0)
          )))

(defn run []
  (.setVisible frame true))
  ;(set-color Color/RED))

(run)
