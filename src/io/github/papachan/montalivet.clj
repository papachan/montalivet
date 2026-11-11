(ns io.github.papachan.montalivet
  "A Clojure library for parsing, sanitising and escaping HTML, built on jsoup."
  (:require [io.github.papachan.montalivet.impl :as impl])
  (:import (org.jsoup.nodes Document Document$OutputSettings Element)))

(set! *warn-on-reflection* true)

(defn parse
  "Parses `input` as a full HTML document and returns a jsoup Document.

  `input` can be a String of HTML, a java.io.File, a java.nio.file.Path or
  a java.io.InputStream. Any other type throws an exception. A stream is
  read to its end but not closed.

  Options:
  - `:pretty-print` boolean, default true.
  - `:escape-mode`  one of :base, :xhtml, :extended.
  - `:base-uri`     base used to resolve relative links. Defaults to \"\"
                    for a String or an InputStream, and to the location of
                    the file for a File or a Path.
  - `:charset`      charset of a File, Path or InputStream, as a string
                    (\"ISO-8859-1\") or a java.nio.charset.Charset. When
                    absent, it is detected from a byte-order mark or a
                    `<meta>` tag, with UTF-8 as the fallback. Ignored for
                    a String."
  (^Document [input] (parse input {}))
  (^Document [input opts] (impl/parse input opts)))

(defn parse-fragment
  "Parses the `html` string as a fragment of a document body, and returns
  a jsoup Document whose body holds the fragment.

  Unlike `parse`, every element stays in the body: `parse` moves elements
  such as `<title>` or `<meta>` to the head.

  Options: `:pretty-print`, `:escape-mode` and `:base-uri`, as in `parse`."
  (^Document [html] (parse-fragment html {}))
  (^Document [html opts] (impl/parse-fragment html opts)))

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

(defn select
  "Returns a vector of the elements of `doc` that match the CSS selector
  `query`, in document order. `doc` can be a Document or any element, in
  which case only its descendants are searched. Returns an empty vector
  when nothing matches.

  See https://jsoup.org/cookbook/extracting-data/selector-syntax for the
  selector syntax. An empty or invalid selector throws an exception."
  [^Element doc query]
  (impl/select doc query))

(defn select-one
  "Returns the first element of `doc` that matches the CSS selector `query`,
  or nil when nothing matches."
  ^Element [^Element doc query]
  (impl/select-one doc query))

(defn text
  "Returns the combined, whitespace-normalised text of `el` and its children."
  ^String [^Element el]
  (impl/text el))

(defn attr
  "Returns the value of attribute `k` of `el`, or nil when it is absent.
  `k` can be a string or a keyword."
  ^String [^Element el k]
  (impl/attr el (name k)))

(defn attrs
  "Returns the attributes of `el` as a map of keyword to string."
  [^Element el]
  (impl/attrs el))

(defn outer-html
  "Returns the markup of `el` itself, including its own tag."
  ^String [^Element el]
  (impl/outer-html el))

(defn clean
  "Removes everything from the `html` string that is not allowed by a
  safelist, so that untrusted markup can be embedded safely. Returns a
  string.

  Options:
  - `:safelist`  one of :none, :simple-text, :basic (default),
                 :basic-with-images, :relaxed.
  - `:add-tags`  tags to allow in addition to the safelist, e.g. [:section].
  - `:remove-tags` tags to disallow.
  - `:add-attributes` map of tag to attributes to allow,
                 e.g. {:a [:target] :all [:class]}.
  - `:remove-attributes` same shape, to disallow attributes.
  - `:base-uri`  base used to resolve relative links, default \"\". Without
                 it, relative links cannot be resolved and are removed.
  - `:preserve-relative-links` keep relative URLs as they are instead of
                 making them absolute. Still requires `:base-uri`.
  - `:pretty-print` boolean, default false.
  - `:escape-mode` one of :base, :xhtml, :extended."
  (^String [html] (clean html {}))
  (^String [html opts] (impl/clean html opts)))