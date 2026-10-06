# Table of contents
-  [`io.github.papachan.montalivet`](#io.github.papachan.montalivet)  - A Clojure library for parsing, sanitising and escaping HTML, built on jsoup.
    -  [`attr`](#io.github.papachan.montalivet/attr) - Returns the value of attribute <code>k</code> of <code>el</code>, or nil when it is absent.
    -  [`attrs`](#io.github.papachan.montalivet/attrs) - Returns the attributes of <code>el</code> as a map of keyword to string.
    -  [`body-html`](#io.github.papachan.montalivet/body-html) - Returns the inner HTML of the document body as a string.
    -  [`clean`](#io.github.papachan.montalivet/clean) - Removes everything from the <code>html</code> string that is not allowed by a safelist, so that untrusted markup can be embedded safely.
    -  [`escape-html`](#io.github.papachan.montalivet/escape-html) - Escapes <code>s</code> so it is safe to embed as HTML text.
    -  [`outer-html`](#io.github.papachan.montalivet/outer-html) - Returns the markup of <code>el</code> itself, including its own tag.
    -  [`parse`](#io.github.papachan.montalivet/parse) - Parses the <code>html</code> string into a jsoup Document.
    -  [`select`](#io.github.papachan.montalivet/select) - Returns a vector of the elements of <code>doc</code> that match the CSS selector <code>query</code>, in document order.
    -  [`select-one`](#io.github.papachan.montalivet/select-one) - Returns the first element of <code>doc</code> that matches the CSS selector <code>query</code>, or nil when nothing matches.
    -  [`text`](#io.github.papachan.montalivet/text) - Returns the combined, whitespace-normalised text of <code>el</code> and its children.

-----
# <a name="io.github.papachan.montalivet">io.github.papachan.montalivet</a>


A Clojure library for parsing, sanitising and escaping HTML, built on jsoup.




## <a name="io.github.papachan.montalivet/attr">`attr`</a><a name="io.github.papachan.montalivet/attr"></a>
``` clojure

(attr el k)
```

Returns the value of attribute `k` of `el`, or nil when it is absent.
  `k` can be a string or a keyword.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L55-L59">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/attrs">`attrs`</a><a name="io.github.papachan.montalivet/attrs"></a>
``` clojure

(attrs el)
```

Returns the attributes of `el` as a map of keyword to string.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L61-L64">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/body-html">`body-html`</a><a name="io.github.papachan.montalivet/body-html"></a>
``` clojure

(body-html doc)
```

Returns the inner HTML of the document body as a string.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L17-L20">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/clean">`clean`</a><a name="io.github.papachan.montalivet/clean"></a>
``` clojure

(clean html)
(clean html opts)
```

Removes everything from the `html` string that is not allowed by a
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
  - `:base-uri`  base used to resolve relative links, default "". Without
                 it, relative links cannot be resolved and are removed.
  - `:preserve-relative-links` keep relative URLs as they are instead of
                 making them absolute. Still requires `:base-uri`.
  - `:pretty-print` boolean, default false.
  - `:escape-mode` one of :base, :xhtml, :extended.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L71-L91">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/escape-html">`escape-html`</a><a name="io.github.papachan.montalivet/escape-html"></a>
``` clojure

(escape-html s)
(escape-html s opts)
```

Escapes `s` so it is safe to embed as HTML text.

  Takes the same `:escape-mode` and `:pretty-print` options as [`parse`](#io.github.papachan.montalivet/parse)
  (default :base).
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L22-L31">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/outer-html">`outer-html`</a><a name="io.github.papachan.montalivet/outer-html"></a>
``` clojure

(outer-html el)
```

Returns the markup of `el` itself, including its own tag.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L66-L69">Source</a></sub></p>

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

## <a name="io.github.papachan.montalivet/select">`select`</a><a name="io.github.papachan.montalivet/select"></a>
``` clojure

(select doc query)
```

Returns a vector of the elements of `doc` that match the CSS selector
  `query`, in document order. `doc` can be a Document or any element, in
  which case only its descendants are searched. Returns an empty vector
  when nothing matches.

  See https://jsoup.org/cookbook/extracting-data/selector-syntax for the
  selector syntax. An empty or invalid selector throws an exception.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L33-L42">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/select-one">`select-one`</a><a name="io.github.papachan.montalivet/select-one"></a>
``` clojure

(select-one doc query)
```

Returns the first element of `doc` that matches the CSS selector `query`,
  or nil when nothing matches.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L44-L48">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/text">`text`</a><a name="io.github.papachan.montalivet/text"></a>
``` clojure

(text el)
```

Returns the combined, whitespace-normalised text of `el` and its children.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L50-L53">Source</a></sub></p>
