(ns org.branimirkostic.supermarketsimulation.gui)


(import '(javax.swing JFrame JPanel JLabel JTextArea JTextField JButton JScrollPane)
        '(java.awt.event ActionListener)
        '(java.awt GridLayout BorderLayout)
        '(java.awt Color))

;----------------------Form Components------------------------------
;form has been done by the mix of technics from sites: http://clojure.org/jvm_hosted and
;http://stuartsierra.com/2010/01/02/first-steps-with-clojure-swing

(def open-button (JButton. "Open"))
(def close-button (JButton. "Close"))
(def dispose-button (JButton. "Reset"))
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
  (.setBounds frame 200 200 700 300)
  (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
  (.setEnabled close-button false)

;------------------Functions and defs-----------------------------
;all basics are from the book: Practical Clojure

;id of the last entered customers
(def customer-id (ref 0))
;number of customers in the supermarket
(def customer-no (ref 0))
;supermarket is working
(def works (ref false))
;max time between two entrances
(def max-entrance-time 3000)
;max shopping time
(def max-shopping-time 3000)
;max paying time
(def max-paying-time 30000)

;-------------

(defn iu-map-key "fn that changes the value of a key in a map" [map-x key-x value-x]
  (dosync
    (alter map-x assoc key-x value-x)))

;-------------

;row of customers for every register
(def register-row-1 (ref {:customer [] :free true}))
(def register-row-2 (ref {:customer [] :free true}))
(def register-row-3 (ref {:customer [] :free true}))
(def register-row-4 (ref {:customer [] :free true}))
(def register-row-5 (ref {:customer [] :free true}))

(defn customer-in "Increasing the number of customers" []
  (dosync
    (ref-set customer-no (inc @customer-no))
    (ref-set customer-id (inc @customer-id))))

(defn customer-out "Decreasing the number of customers" []
  (dosync(ref-set customer-no (dec @customer-no))))

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

(defn register-of-the-row "Returns a register of the row" [random-no]
  (cond
    (= random-no register-row-1) [register-label 1 register-textarea]
    (= random-no register-row-2) [register-label2 2 register-textarea2]
    (= random-no register-row-3) [register-label3 3 register-textarea3]
    (= random-no register-row-4) [register-label4 4 register-textarea4]
    (= random-no register-row-5) [register-label5 5 register-textarea5]))

(defn write-string "returns a string from the vector"
  ([vector-x] (write-string vector-x 0 ""))
  ([vector-x i st]
    (if (= (get vector-x i) nil)
      st
      (recur vector-x (inc i) (str st "\n" ((get vector-x i) :id))))))

(defn go-to-register "customer is on a register" [row]
  (loop [i 0]
    (when (and (= (row :free) true) (not (= (get (@row :customer) 0) nil)))
      (iu-map-key row :free false)
      (.setText (get (register-of-the-row row) 0)
                (str "Register" (get (register-of-the-row row) 1) ": " ((get (@row :customer) 0) :id) " "))
      (.setText (get (register-of-the-row row) 2) (write-string (subvec (@row :customer) 1)))
      (Thread/sleep  ((get (@row :customer) 0) :max-paying-time))
      (println (str "Payed" (get (@row :customer) 0)))
      (.setText (get (register-of-the-row row) 0)
                (str " Register" (get (register-of-the-row row) 1) ": "))
      (iu-map-key row :free true)
      (iu-map-key row :customer (subvec (@row :customer) 1))
      (customer-out)
      (.setText customers-label (str "Customers: " @customer-no))
      (recur i))))

(defn enter-row "customer is entering a row" [customer row]
	((Thread/sleep (customer :max-shopping-time))
   (.setText (get (random-row row) 0) (str (.getText (get (random-row row) 0)) "\n" (customer :id)))
   ;(dosync(ref-set (get (random-row row) 1) (conj @(get (random-row row) 1) customer)))
   (iu-map-key (get (random-row row) 1) :customer (conj (@(get (random-row row) 1) :customer) customer))
   (pvalues (go-to-register (get (random-row row) 1)))
   (println (get (random-row row) 1))
   (println (str "Row entry" customer))))

(defn run-entrance "creating a customer who enters the supermarket" []
  (loop [i 0]
    (when (= @works true)
      (def entrance-x (rand-int 3))
      (Thread/sleep (rand max-entrance-time))
      (customer-in)
      (def customer (agent {:id @customer-id
                            :max-shopping-time (rand max-shopping-time)
                            :max-paying-time (rand max-paying-time)}))
      (pvalues (enter-row @customer (rand-int 5)))
      (.setText customers-label (str "Customers: " @customer-no))
      (.setText (random-entrance entrance-x) (str "  Entrance" (inc entrance-x) ": " @customer-id " "))
      (println (str "Supermarket entry" @customer))
      (recur i))))

(defn open "fn that opens the supermarket" []
  (ref-set works true))
(defn close "fn that closes the supermarket" []
  (ref-set works false))

(defn reset []
  (Thread/sleep 3000))

(defn reset-customer-no []
  (ref-set customer-no 0))

(defn reset-customer-id []
  (ref-set customer-id 0))

;taken from https://github.com/clojure/clojure/blob/553f4879cad019dd9dc1727165d8a41c216bd086/src/clj/clojure/repl.clj#L270
(defn thread-stopper
  "Returns a function that takes one arg and uses that as an exception message
  to stop the given thread.  Defaults to the current thread"
  ([] (thread-stopper (Thread/currentThread)))
  ([thread] (fn [msg] (.stop thread (Error. msg)))))

;----------------------Action Listeners------------------------------
;action listeners have been done by the technics from site: http://clojure.org/jvm_hosted

(.addActionListener open-button
      (proxy [ActionListener] []
        (actionPerformed [evt]
                         (dosync(open))
                         (pcalls run-entrance)
                         ;(.start (Thread. #(run-entrance)))
                         (.setEnabled open-button false)
                         (.setEnabled close-button true)
                         (.setEnabled dispose-button false)
                         (.setBackground west-panel Color/GREEN)
                         (.setBackground north-panel Color/GREEN)
                         (.setBackground regs-panel Color/GREEN)
                         (.setBackground rows-panel Color/GREEN))))
(.addActionListener close-button
      (proxy [ActionListener] []
        (actionPerformed [evt]
                         (dosync(close))
                         (.setEnabled open-button true)
                         (.setEnabled close-button false)
                         (.setEnabled dispose-button true)
                         (.setBackground west-panel Color/YELLOW)
                         (.setBackground north-panel Color/YELLOW)
                         (.setBackground regs-panel Color/YELLOW)
                         (.setBackground rows-panel Color/YELLOW))))
(.addActionListener dispose-button
      (proxy [ActionListener] []
        (actionPerformed [evt]
                         (reset)
                         (thread-stopper (Thread/currentThread))
                         (dosync(close))
                         (dosync(reset-customer-no))
                         (dosync(reset-customer-id))
                         (.setText customers-label "Customers:  0")
                         (.setText entrance-label "  Entrance1:  ")
                         (.setText entrance-label2 "  Entrance2:  ")
                         (.setText entrance-label3 "  Entrance3:  ")
                         (.setText register-label " Register1: ")
                         (.setText register-label2 " Register2: ")
                         (.setText register-label3 " Register3: ")
                         (.setText register-label4 " Register4: ")
                         (.setText register-label5 " Register5: ")
                         (.setText register-textarea "")
                         (.setText register-textarea2 "")
                         (.setText register-textarea3 "")
                         (.setText register-textarea4 "")
                         (.setText register-textarea5 "")
                         (iu-map-key register-row-1 :free true)
                         (iu-map-key register-row-1 :customer [])
                         (iu-map-key register-row-2 :free true)
                         (iu-map-key register-row-2 :customer [])
                         (iu-map-key register-row-3 :free true)
                         (iu-map-key register-row-3 :customer [])
                         (iu-map-key register-row-4 :free true)
                         (iu-map-key register-row-4 :customer [])
                         (iu-map-key register-row-5 :free true)
                         (iu-map-key register-row-5 :customer [])
                         (.setBackground west-panel Color/RED)
                         (.setBackground north-panel Color/RED)
                         (.setBackground regs-panel Color/RED)
                         (.setBackground rows-panel Color/RED))))

(defn run []
  (.setVisible frame true)
  (.setBackground west-panel Color/RED)
  (.setBackground north-panel Color/RED)
  (.setBackground regs-panel Color/RED)
  (.setBackground rows-panel Color/RED))

(run)
