(ns ^:no-doc io.github.papachan.montalivet.impl
  "Jsoup interop. Not part of the public API."
  (:import
   (org.jsoup Jsoup)
   (org.jsoup.nodes Document Document$OutputSettings Entities Entities$EscapeMode)))

(set! *warn-on-reflection* true)

(def escape-modes
  {:base     Entities$EscapeMode/base
   :xhtml    Entities$EscapeMode/xhtml
   :extended Entities$EscapeMode/extended})

(defn escape-mode
  "Returns the jsoup EscapeMode for keyword `k`."
  ^Entities$EscapeMode [k]
  (or (get escape-modes k)
      (throw (ex-info "Unknown escape mode"
                      {:escape-mode k :valid (keys escape-modes)}))))

(defn apply-output-settings!
  "Mutates the output settings of `doc` according to `opts`.
  Returns `doc`."
  ^Document [^Document doc {:keys [pretty-print escape-mode-key]
                            :or   {pretty-print true}}]
  (let [^Document$OutputSettings settings (.outputSettings doc)]
    (.prettyPrint settings (boolean pretty-print))
    (when escape-mode-key
      (.escapeMode settings (escape-mode escape-mode-key))))
  doc)

(defn parse
  ^Document [^String html opts]
  (apply-output-settings! (Jsoup/parse html)
                          (-> opts
                              (select-keys [:pretty-print])
                              (assoc :escape-mode-key (:escape-mode opts)))))

(defn body-html
  ^String [^Document doc]
  (.html (.body doc)))

(defn escape-html
  ^String [^String s ^Document$OutputSettings settings]
  (Entities/escape s settings))

(defn output-settings
  ^Document$OutputSettings [^Document doc]
  (.outputSettings doc))
