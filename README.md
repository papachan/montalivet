# Montalivet

[![Clojars Project](https://img.shields.io/clojars/v/io.github.papachan/montalivet.svg)](https://clojars.org/io.github.papachan/montalivet)

### Clojure HTML toolkit: parse, clean and escape, powered by jsoup.

A small Clojure wrapper around [jsoup](https://jsoup.org/), so you can parse
HTML, escape text and sanitise untrusted markup from Clojure without writing
Java interop yourself.

```clj
(require '[io.github.papachan.montalivet :as m])

(-> (m/parse "<p>1 < 2</p>" {:pretty-print false :escape-mode :extended})
    m/body-html)
;; => "<p>1 &lt; 2</p>"

(m/escape-html "<b>a & b</b>")
;; => "&lt;b&gt;a &amp; b&lt;/b&gt;"

(m/clean "<p onclick=\"x()\">Hi <script>alert(1)</script></p>")
;; => "<p>Hi </p>"
```

## Installation

Add it to your `deps.edn`:

```clj
io.github.papachan/montalivet {:mvn/version "0.1.3"}
```

Install it locally from a clone:

```sh
clojure -T:build install
```

## Compatibility

Montalivet runs on the JVM only. It is a wrapper around the Java library
jsoup, and it is published as `.clj` files, so it **cannot be used from
ClojureScript**.

## Usage

Everything public is in `io.github.papachan.montalivet`.

### `parse`

```clj
(m/parse input)
(m/parse input opts)
```

Parses a full HTML document and returns a jsoup `Document`. `input` can be:

- a `String` of HTML,
- a `java.io.File`,
- a `java.nio.file.Path`,
- a `java.io.InputStream` (read to its end, but not closed).

```clj
(m/parse "<p>hi</p>")
(m/parse (java.io.File. "page.html"))
(m/parse (java.nio.file.Path/of "page.html" (make-array String 0)) {:charset "ISO-8859-1"})
```

Now parse accepts input-streams:

```
(with-open [in (clojure.java.io/input-stream "links.html")]
  (mapv m/text (m/select (m/parse in) "a")))
;; => ["jsoup" "Clojure" "About & contact"]
```

Select now the correct charset with `parse` method:

```
(def latin1-file
  (doto (java.io.File/createTempFile "montalivet" ".html")
    (.deleteOnExit)
    (spit "<p>caf�</p>" :encoding "ISO-8859-1")))

(m/select-one (m/parse latin1-file {:charset "ISO-8859-1"}) "p")
```

If charset is incorrect you catch an Exception.

```
(try
   (m/parse latin1-file {:charset :latin1})
   (catch clojure.lang.ExceptionInfo e
     [(ex-message e) (ex-data e)]))
```



| Option          | Default | Description                                   |
|-----------------|---------|-----------------------------------------------|
| `:pretty-print` | `true`  | Reformat the output when the document is rendered. |
| `:escape-mode`  | jsoup's default | `:base`, `:xhtml` or `:extended`.     |
| `:base-uri`     | `""`, or the file's location for a `File` or `Path` | Base used to resolve relative links. |
| `:charset`      | detected | Charset of a `File`, `Path` or `InputStream`: a string such as `"ISO-8859-1"`, or a `java.nio.charset.Charset`. When absent, jsoup reads it from a byte-order mark or a `<meta>` tag, and falls back to UTF-8. Ignored for a `String`. |

An unknown `:escape-mode`, an invalid `:charset` or any other type of `input`
throws an `ex-info`.

### `parse-fragment`

```clj
(m/parse-fragment html)
(m/parse-fragment html opts)
```

Parses an HTML string as a fragment of a document body. Returns a jsoup
`Document` whose body holds the fragment, so `body-html` and `select` work on
it. Takes the `:pretty-print`, `:escape-mode` and `:base-uri` options of
`parse`.

Unlike `parse`, every element stays in the body:

```clj
(def html "<title>t</title><p>x</p>")

(m/body-html (m/parse-fragment html {:pretty-print false}))
;; => "<title>t</title><p>x</p>"

(m/body-html (m/parse html {:pretty-print false}))
;; => "<p>x</p>"   ; the title went to the head
```

Works with hickory:


```
(require '[hickory.core :as h])
(h/as-hickory (.body (m/parse-fragment "<a href=\"foo\">foo</a>")))
```


### `body-html`

```clj
(m/body-html doc)
```

Returns the inner HTML of the document's `<body>` as a string. Fragments
without a `<body>` are wrapped in one by jsoup, so this works on partial
markup too:

```clj
(-> (m/parse "<b>a</b><i>b</i>" {:pretty-print false})
    m/body-html)
;; => "<b>a</b><i>b</i>"
```

### `escape-html`

```clj
(m/escape-html s)
(m/escape-html s opts)
```

Escapes a string so it can be embedded as HTML text. It takes the same
`:escape-mode` option as `parse` (default `:base`).

```clj
(m/escape-html "a b" {:escape-mode :extended})
;; => "a&nbsp;b"

(m/escape-html "a b" {:escape-mode :xhtml})
;; => "a&#xa0;b"   ; XHTML has no &nbsp;
```

Jsoup only escapes characters the output charset cannot represent. With the
default UTF-8, text such as `café` is returned unchanged, so this is not
identical to Apache Commons Text's `escapeHtml4`.

### `clean`

```clj
(m/clean html)
(m/clean html opts)
```

Removes everything that is not allowed by a safelist, so untrusted markup
can be embedded safely. Returns a string.

```clj
(m/clean "<p onclick=\"x()\">Hi <script>alert(1)</script><b>there</b></p>")
;; => "<p>Hi <b>there</b></p>"

;; keep only the text
(m/clean "<p>Hi <b>there</b></p>" {:safelist :none})
;; => "Hi there"

;; allow extra tags and attributes
(m/clean "<section class=\"c\"><b>x</b></section>"
         {:add-tags [:section] :add-attributes {:section [:class]}})
;; => "<section class=\"c\"><b>x</b></section>"

;; relative links need a base URI, and unsafe ones lose their href
(m/clean "<a href=\"/a\">l</a>" {:base-uri "https://example.com/"})
;; => "<a href=\"https://example.com/a\">l</a>"

(m/clean "<a href=\"javascript:alert(1)\">l</a>" {:base-uri "https://example.com/"})
;; => "<a rel=\"nofollow\">l</a>"
```

| Option                    | Default  | Description                                              |
|---------------------------|----------|----------------------------------------------------------|
| `:safelist`               | `:basic` | `:none`, `:simple-text`, `:basic`, `:basic-with-images` or `:relaxed`. |
| `:add-tags`, `:remove-tags` |        | Tags to allow or disallow, such as `[:section]`.         |
| `:add-attributes`, `:remove-attributes` | | Map of tag to attributes, such as `{:a [:target] :all [:class]}`. `:all` applies to every tag. |
| `:base-uri`               | `""`     | Base used to resolve relative links. Without it they are removed. |
| `:preserve-relative-links`| `false`  | Keep relative URLs as they are. Still requires `:base-uri`. |
| `:pretty-print`           | `false`  | Reformat the output.                                     |
| `:escape-mode`            |          | `:base`, `:xhtml` or `:extended`.                        |

An unknown `:safelist` or `:escape-mode` throws an `ex-info` listing the
valid values. The exact output can differ between jsoup versions, so check
it against the version you use.

### `select` and `select-one`

```clj
(m/select doc css-query)
(m/select-one doc css-query)
```

Find elements of a parsed document with a
[CSS selector](https://jsoup.org/cookbook/extracting-data/selector-syntax).
`select` returns a vector of matching elements in document order (empty when
nothing matches), and `select-one` returns the first match or `nil`. Both work
on a document or on any element, in which case only its descendants are
searched. An empty or invalid selector throws an exception.

```clj
(def doc (m/parse "<div id=\"a\" class=\"x y\">
                     <a href=\"/1\" title=\"one\">One</a>
                     <a href=\"/2\">Two</a>
                   </div>"))

(map m/text (m/select doc "a"))
;; => ("One" "Two")

(map #(m/attr % :href) (m/select doc "a[href]"))
;; => ("/1" "/2")

(m/select doc "table")
;; => []

(m/select-one doc "table")
;; => nil
```

### Reading elements

| Function            | Returns                                                   |
|---------------------|-----------------------------------------------------------|
| `(m/text el)`       | The element's text, with entities decoded.                |
| `(m/attr el k)`     | The value of attribute `k` (string or keyword), or `nil`. |
| `(m/attrs el)`      | All attributes as a map of keyword to string.             |
| `(m/outer-html el)` | The element's own markup, including its tag.              |

```clj
(m/attrs (m/select-one doc "div"))
;; => {:id "a", :class "x y"}

(m/outer-html (m/select-one doc "a"))
;; => "<a href=\"/1\" title=\"one\">One</a>"
```

## Documentation

You can find the documentation here: [API](https://github.com/papachan/montalivet/blob/main/API.md).

## Development

Run the tests with [Kaocha](https://github.com/lambdaisland/kaocha):

```sh
clojure -M:dev:test:runner
```

Other tasks:

```sh
clojure -T:build jar        # build target/montalivet-<version>.jar and pom.xml
clojure -T:build uberjar    # standalone jar with all dependencies
clojure -T:build install    # install into ~/.m2
clojure -M:clj-kondo --lint src test build.clj
clojure -M:format-check

```

See [CONTRIBUTING.md](CONTRIBUTING.md) and the [CHANGELOG](CHANGELOG.md).

## License

Copyright &copy; 2026 papachan.

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE).
