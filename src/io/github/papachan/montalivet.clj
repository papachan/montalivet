(ns io.github.papachan.montalivet
  "Clojure wrapper around jsoup."
  (:require [io.github.papachan.montalivet.impl :as impl])
  (:import (org.jsoup.nodes Document Document$OutputSettings)))

(set! *warn-on-reflection* true)

(defn parse
  "Parses the `html` string into a jsoup Document.

  Options:
  - `:pretty-print` boolean, default true.
  - `:escape-mode`  one of :base, :xhtml, :extended."
  (^Document [html] (parse html {}))
  (^Document [html opts] (impl/parse html opts)))

(defn body-html
  "Returns the inner HTML of the document body as a string."
  ^String [^Document doc]
  (impl/body-html doc))

(defn escape-html
  "Escapes `s` so it is safe to embed as HTML text.

  Takes the same `:escape-mode` and `:pretty-print` options as `parse`
  (default :base)."
  (^String [s] (escape-html s {}))
  (^String [s opts]
   (let [^Document doc (impl/parse "" (assoc opts :escape-mode (:escape-mode opts :base)))
         ^Document$OutputSettings settings (impl/output-settings doc)]
     (impl/escape-html s settings))))
