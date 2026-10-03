# Table of contents
-  [`io.github.papachan.montalivet`](#io.github.papachan.montalivet)  - Clojure wrapper around jsoup.
    -  [`body-html`](#io.github.papachan.montalivet/body-html) - Returns the inner HTML of the document body as a string.
    -  [`escape-html`](#io.github.papachan.montalivet/escape-html) - Escapes <code>s</code> so it is safe to embed as HTML text.
    -  [`parse`](#io.github.papachan.montalivet/parse) - Parses the <code>html</code> string into a jsoup Document.

-----
# <a name="io.github.papachan.montalivet">io.github.papachan.montalivet</a>


Clojure wrapper around jsoup.




## <a name="io.github.papachan.montalivet/body-html">`body-html`</a><a name="io.github.papachan.montalivet/body-html"></a>
``` clojure

(body-html doc)
```

Returns the inner HTML of the document body as a string.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L17-L20">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/escape-html">`escape-html`</a><a name="io.github.papachan.montalivet/escape-html"></a>
``` clojure

(escape-html s)
(escape-html s opts)
```

Escapes `s` so it is safe to embed as HTML text.

  Takes the same `:escape-mode` and `:pretty-print` options as [`parse`](#io.github.papachan.montalivet/parse)
  (default :base).
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L22-L31">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/parse">`parse`</a><a name="io.github.papachan.montalivet/parse"></a>
``` clojure

(parse html)
(parse html opts)
```

Parses the `html` string into a jsoup Document.

  Options:
  - `:pretty-print` boolean, default true.
  - `:escape-mode`  one of :base, :xhtml, :extended.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L8-L15">Source</a></sub></p>
