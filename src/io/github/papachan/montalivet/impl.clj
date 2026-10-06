(ns ^:no-doc io.github.papachan.montalivet.impl
  "Jsoup interop. Not part of the public API."
  (:import
   (org.jsoup Jsoup)
   (org.jsoup.nodes Attribute Document Document$OutputSettings Element Entities
                    Entities$EscapeMode)
   (org.jsoup.safety Safelist)))

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

(defn select
  "Elements of `el` (a Document or Element) matching the CSS `query`,
  as a vector."
  [^Element el ^String query]
  (vec (.select el query)))

(defn select-one
  "First element of `el` matching the CSS `query`, or nil."
  ^Element [^Element el ^String query]
  (.selectFirst el query))

(defn text
  ^String [^Element el]
  (.text el))

(defn attr
  "Value of attribute `k` on `el`, or nil when it is not present."
  ^String [^Element el ^String k]
  (when (.hasAttr el k)
    (.attr el k)))

(defn attrs
  "Attributes of `el` as a map of keyword to string."
  [^Element el]
  (persistent!
   (reduce (fn [m ^Attribute a]
             (assoc! m (keyword (.getKey a)) (.getValue a)))
           (transient {})
           (.attributes el))))

(defn outer-html
  ^String [^Element el]
  (.outerHtml el))

(defn escape-html
  ^String [^String s ^Document$OutputSettings settings]
  (Entities/escape s settings))

(defn output-settings
  ^Document$OutputSettings [^Document doc]
  (.outputSettings doc))

(def safelists
  {:none              (fn [] (Safelist/none))
   :simple-text       (fn [] (Safelist/simpleText))
   :basic             (fn [] (Safelist/basic))
   :basic-with-images (fn [] (Safelist/basicWithImages))
   :relaxed           (fn [] (Safelist/relaxed))})

(defn- strings
  ^"[Ljava.lang.String;" [coll]
  (into-array String (map name coll)))

(defn- tag-name
  "Tag name for attribute rules. jsoup uses the literal \":all\" for
  rules that apply to every tag, so `:all` maps to that."
  ^String [tag]
  (if (= :all tag) ":all" (name tag)))

(defn safelist
  "Builds a jsoup Safelist from the `:safelist` keyword and the
  customisation options in `opts`. Always returns a new, mutable Safelist."
  ^Safelist [{:keys [safelist add-tags remove-tags add-attributes
                     remove-attributes preserve-relative-links]
              :or   {safelist :basic}}]
  (let [make (or (get safelists safelist)
                 (throw (ex-info "Unknown safelist"
                                 {:safelist safelist :valid (keys safelists)})))
        ^Safelist sl (make)]
    (when (seq add-tags)
      (.addTags sl (strings add-tags)))
    (when (seq remove-tags)
      (.removeTags sl (strings remove-tags)))
    (doseq [[tag attrs] add-attributes]
      (.addAttributes sl (tag-name tag) (strings attrs)))
    (doseq [[tag attrs] remove-attributes]
      (.removeAttributes sl (tag-name tag) (strings attrs)))
    (when preserve-relative-links
      (.preserveRelativeLinks sl true))
    sl))

(defn clean
  "Removes everything in `html` that is not allowed by the safelist
  described in `opts`. Returns a string."
  ^String [^String html {:keys [base-uri pretty-print]
                         :or   {base-uri "" pretty-print false}
                         :as   opts}]
  (let [^Document$OutputSettings settings (Document$OutputSettings.)]
    (.prettyPrint settings (boolean pretty-print))
    (when-let [mode (:escape-mode opts)]
      (.escapeMode settings (escape-mode mode)))
    (Jsoup/clean html ^String base-uri (safelist opts) settings)))