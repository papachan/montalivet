# Montalivet

[![Clojars Project](https://img.shields.io/clojars/v/io.github.papachan/montalivet.svg)](https://clojars.org/io.github.papachan/montalivet)

A small Clojure wrapper around [jsoup](https://jsoup.org/), so you can parse
HTML and escape text from Clojure without writing Java interop yourself.

```clj
(require '[io.github.papachan.montalivet :as m])

(-> (m/parse "<p>1 < 2</p>" {:pretty-print false :escape-mode :extended})
    m/body-html)
;; => "<p>1 &lt; 2</p>"

(m/escape-html "<b>a & b</b>")
;; => "&lt;b&gt;a &amp; b&lt;/b&gt;"
```

## Installation

Add it to your `deps.edn`:

```clj
io.github.papachan/montalivet {:mvn/version "0.1.0"}
```

Install it locally from a clone:

```sh
clojure -T:build install
```

## Usage

Everything public is in `io.github.papachan.montalivet`.

### `parse`

```clj
(m/parse html)
(m/parse html opts)
```

Parses an HTML string and returns a jsoup `Document`.

| Option          | Default | Description                                   |
|-----------------|---------|-----------------------------------------------|
| `:pretty-print` | `true`  | Reformat the output when the document is rendered. |
| `:escape-mode`  | jsoup's default | `:base`, `:xhtml` or `:extended`.     |

An unknown `:escape-mode` throws an `ex-info` listing the valid modes.

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
clojure -M:clj-kondo --lint src test
```

See [CONTRIBUTING.md](CONTRIBUTING.md) and the [CHANGELOG](CHANGELOG.md).

## License

Copyright &copy; 2026 papachan.

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE).
