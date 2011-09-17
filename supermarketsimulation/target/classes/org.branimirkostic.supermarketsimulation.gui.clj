(ns org.branimirkostic.supermarketsimulation.gui)

(import '(javax.swing JFrame JPanel JLabel JTextArea JTextField JButton JScrollPane)
        '(java.awt.event ActionListener)
        '(java.awt GridLayout BorderLayout))

(defn supermarket []
  (let [frame (JFrame. "Supermarket")
        north-panel (JPanel.)
        west-panel (JPanel.)
        center-panel (JPanel.)
        regs-panel (JPanel.)
        rows-panel (JPanel.)
        open-button (JButton. "Open")
        close-button (JButton. "Close")
        dispose-button (JButton. "Dispose")
        customers-label(JLabel. "Customers: ")
        customers-no-label(JLabel. "@")
        entrance-label (JLabel. "Entrance")
        register-label (JLabel. "Register")
        register-textarea (JTextArea.)
        register-textarea-scroll(JScrollPane. register-textarea 22 32)
        ]
    
    (doto regs-panel
      (.setLayout (GridLayout. 1 1))
      (.add register-label))
    (doto rows-panel
      (.setLayout (GridLayout. 1 1))
      (.add register-textarea-scroll))
    (doto north-panel
      (.add open-button)
      (.add close-button)
      (.add dispose-button)
      (.add customers-label))
    (doto west-panel
      (.setLayout (GridLayout. 0 1))
      (.add entrance-label))
    (doto center-panel
      (.setLayout (BorderLayout.))
      (.add regs-panel "North")
      (.add rows-panel "Center"))
    (doto frame
      (.add north-panel "North")
      (.add west-panel "West")
      (.add center-panel "Center")
      (.setBounds 200 200 500 200)
      ;(.setSize 500 200)
      (.setVisible true))))
(supermarket)



