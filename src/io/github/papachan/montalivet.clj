(ns io.github.papachan.montalivet
  "A Clojure library for parsing, sanitising and escaping HTML, built on jsoup."
  (:require [io.github.papachan.montalivet.impl :as impl])
  (:import (org.jsoup.nodes Document Document$OutputSettings Element)))

(set! *warn-on-reflection* true)

(defn version
  "Returns the version of montalivet as a string, e.g. \"0.1.3\"."
  ^String []
  @impl/version)

(defn jsoup-version
  "Returns the version of the jsoup library found on the classpath, as a
  string. It can differ from the version montalivet was built with when
  another dependency brings its own jsoup. The output of `clean` and
  `escape-html` can change between jsoup versions."
  ^String []
  @impl/jsoup-version)

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

(defn unescape-html
  "Decodes the HTML entities in `s`: named (`&amp;`, `&copy;`), decimal
  (`&#66;`) and hexadecimal (`&#x41;`). Unknown entities are left as they
  are. It reverses `escape-html`.

  Options:
  - `:in-attribute` boolean, default false. Decodes `s` as an attribute
    value: a named entity without its `;` is then kept as it is when it is
    followed by `=` or by a letter or digit, as in the URL `a=1&copy=2`."
  (^String [s] (unescape-html s {}))
  (^String [s opts] (impl/unescape-html s opts)))

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

(defn abs-url
  "Returns the value of attribute `k` of `el` as an absolute URL, resolved
  against the base URI of the document. Returns nil when the attribute is
  absent, or when it is relative and the document has no base URI, so it
  works with `if-let`, `some->` and `keep`. jsoup returns \"\" in both
  cases. `k` can be a string or a keyword.

  The URL is not checked: a `javascript:` link is returned as it is. Use
  `clean` or `valid?` on untrusted markup."
  ^String [^Element el k]
  (impl/abs-url el (name k)))

(defn base-uri
  "Returns the base URI that `el` (a Document or any element) uses to
  resolve relative links in `abs-url`, or nil when it has none. jsoup
  returns \"\" then.

  It is the `:base-uri` given to `parse` or `parse-fragment`, or for a File
  or a Path without that option, the absolute path of the file. A `<base
  href>` tag in the document replaces it, resolved against it when it is
  relative."
  ^String [^Element el]
  (impl/base-uri el))

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

(defn valid?
  "Returns true when the `html` string contains nothing that `clean` would
  remove with the same safelist, so it can be used as it is. Use it to
  reject untrusted markup instead of cleaning it.

  Like `clean`, it checks a fragment of a body: a full document with
  `<html>` or `<head>` is never valid.

  Options: `:safelist`, `:add-tags`, `:remove-tags`, `:add-attributes`,
  `:remove-attributes` and `:preserve-relative-links`, as in `clean`. There
  is no base URI, so relative links are only valid with
  `:preserve-relative-links`."
  ([html] (valid? html {}))
  ([html opts] (impl/valid? html opts)))
