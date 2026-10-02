## Changelog

This is a history of changes to [montalivet](https://github.com/papachan/montalivet).

#### 0.1.0 - Unreleased

* First release: a Clojure wrapper around [jsoup](https://jsoup.org/).
* Added `parse`, which parses an HTML string into a jsoup `Document`, with the
  `:pretty-print` and `:escape-mode` (`:base`, `:xhtml`, `:extended`) options.
* Added `body-html`, which returns the inner HTML of a document body.
* Added `escape-html`, which escapes a string for use as HTML text.
