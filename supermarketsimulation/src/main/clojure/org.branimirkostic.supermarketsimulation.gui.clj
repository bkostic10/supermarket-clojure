(ns org.branimirkostic.supermarketsimulation.gui)


(import '(javax.swing JFrame JPanel JLabel JTextArea JTextField JButton JScrollPane)
        '(java.awt.event ActionListener)
        '(java.awt GridLayout BorderLayout))

;----------------------Form Components------------------------------

(def open-button (JButton. "Open"))
(def close-button (JButton. "Close"))
(def dispose-button (JButton. "Dispose"))
(def customers-label (JLabel. "Customers:  0"))
(def entrance-label (JLabel. "  Entrance1:     "))
(def entrance-label2 (JLabel. "  Entrance2:     "))
(def entrance-label3 (JLabel. "  Entrance3:     "))
(def register-label (JLabel. "Register1: "))
(def register-label2 (JLabel. "Register2: "))
(def register-label3 (JLabel. "Register3: "))
(def register-label4 (JLabel. "Register4: "))
(def register-label5 (JLabel. "Register5: "))
(def register-textarea (JTextArea.))
(.setEnabled register-textarea false)
(def register-textarea2 (JTextArea.))
(.setEnabled register-textarea2 false)
(def register-textarea3 (JTextArea.))
(.setEnabled register-textarea3 false)
(def register-textarea4 (JTextArea.))
(.setEnabled register-textarea4 false)
(def register-textarea5 (JTextArea.))
(.setEnabled register-textarea5 false)
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

(def customer-no (ref 0))
(def works (ref false))
(def max-entrance-time 4000)

(def max-shopping-time 4000)

(defstruct customer :id :max-shopping-time)

(defn customer-in [] (dosync(ref-set customer-no (inc @customer-no))))

(defn run-entrance []
  (loop [i 0]
    (when (= @works true)
      (Thread/sleep (rand max-entrance-time))
      (customer-in)
      (agent (struct-map customer :id @customer-no :max-shopping-time (rand max-shopping-time)))
      (println (str "Customers: " @customer-no))
      (.setText customers-label (str "Customers: " @customer-no))
      (recur i))))

(defn open []
  (ref-set works true))
(defn close []
  (ref-set works false))

;----------------------Action Listeners------------------------------

(.addActionListener open-button
      (proxy [ActionListener] []
        (actionPerformed [evt]
                         (dosync(open))
                         (pcalls run-entrance)
                         (.setEnabled open-button false)
                         (.setEnabled close-button true)
                         ;(.start (Thread. #(run-entrance)))
                         )))
(.addActionListener close-button
      (proxy [ActionListener] []
        (actionPerformed [evt]
          (dosync(close))
          (println "Stopped")
          (.setEnabled open-button true)
          (.setEnabled close-button false)
          ;(.setText register-textarea2 "")
          )))
(.addActionListener dispose-button
      (proxy [ActionListener] []
        (actionPerformed [evt]
            (System/exit 0)
          )))

(defn run []
  (.setVisible frame true))

(run)
