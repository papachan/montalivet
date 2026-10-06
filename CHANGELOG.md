## Changelog

This is a history of changes to [montalivet](https://github.com/papachan/montalivet).

#### 0.1.2 - 2026-10-06

* Added `select` and `select-one`, which find elements of a document with a
  CSS selector.
* Added `text`, `attr`, `attrs` and `outer-html` to read the matched elements.

#### 0.1.1 - 2026-10-05

* Added `clean`, which removes unsafe markup from an HTML string using a jsoup
  safelist. Options: `:safelist` (`:none`, `:simple-text`, `:basic`,
  `:basic-with-images`, `:relaxed`), `:add-tags`, `:remove-tags`,
  `:add-attributes`, `:remove-attributes`, `:base-uri`,
  `:preserve-relative-links`, `:pretty-print` and `:escape-mode`.
* Updated jsoup from 1.18.1 to 1.23.2. The output of `clean` can differ
  between jsoup versions. For example, the `rel="nofollow"` attribute is no
  longer added to links that keep a valid `href`.

#### 0.1.0 - 2026-10-02

* First release: a Clojure wrapper around [jsoup](https://jsoup.org/).
* Added `parse`, which parses an HTML string into a jsoup `Document`, with the
  `:pretty-print` and `:escape-mode` (`:base`, `:xhtml`, `:extended`) options.
* Added `body-html`, which returns the inner HTML of a document body.
* Added `escape-html`, which escapes a string for use as HTML text.
