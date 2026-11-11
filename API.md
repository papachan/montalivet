# Table of contents
-  [`io.github.papachan.montalivet`](#io.github.papachan.montalivet)  - A Clojure library for parsing, sanitising and escaping HTML, built on jsoup.
    -  [`attr`](#io.github.papachan.montalivet/attr) - Returns the value of attribute <code>k</code> of <code>el</code>, or nil when it is absent.
    -  [`attrs`](#io.github.papachan.montalivet/attrs) - Returns the attributes of <code>el</code> as a map of keyword to string.
    -  [`body-html`](#io.github.papachan.montalivet/body-html) - Returns the inner HTML of the document body as a string.
    -  [`clean`](#io.github.papachan.montalivet/clean) - Removes everything from the <code>html</code> string that is not allowed by a safelist, so that untrusted markup can be embedded safely.
    -  [`escape-html`](#io.github.papachan.montalivet/escape-html) - Escapes <code>s</code> so it is safe to embed as HTML text.
    -  [`outer-html`](#io.github.papachan.montalivet/outer-html) - Returns the markup of <code>el</code> itself, including its own tag.
    -  [`parse`](#io.github.papachan.montalivet/parse) - Parses <code>input</code> as a full HTML document and returns a jsoup Document.
    -  [`parse-fragment`](#io.github.papachan.montalivet/parse-fragment) - Parses the <code>html</code> string as a fragment of a document body, and returns a jsoup Document whose body holds the fragment.
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
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L78-L82">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/attrs">`attrs`</a><a name="io.github.papachan.montalivet/attrs"></a>
``` clojure

(attrs el)
```

Returns the attributes of `el` as a map of keyword to string.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L84-L87">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/body-html">`body-html`</a><a name="io.github.papachan.montalivet/body-html"></a>
``` clojure

(body-html doc)
```

Returns the inner HTML of the document body as a string.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L40-L43">Source</a></sub></p>

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
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L94-L114">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/escape-html">`escape-html`</a><a name="io.github.papachan.montalivet/escape-html"></a>
``` clojure

(escape-html s)
(escape-html s opts)
```

Escapes `s` so it is safe to embed as HTML text.

  Takes the same `:escape-mode` and `:pretty-print` options as [`parse`](#io.github.papachan.montalivet/parse)
  (default :base).
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L45-L54">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/outer-html">`outer-html`</a><a name="io.github.papachan.montalivet/outer-html"></a>
``` clojure

(outer-html el)
```

Returns the markup of `el` itself, including its own tag.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L89-L92">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/parse">`parse`</a><a name="io.github.papachan.montalivet/parse"></a>
``` clojure

(parse input)
(parse input opts)
```

Parses `input` as a full HTML document and returns a jsoup Document.

  `input` can be a String of HTML, a java.io.File, a java.nio.file.Path or
  a java.io.InputStream. Any other type throws an exception. A stream is
  read to its end but not closed.

  Options:
  - `:pretty-print` boolean, default true.
  - `:escape-mode`  one of :base, :xhtml, :extended.
  - `:base-uri`     base used to resolve relative links. Defaults to ""
                    for a String or an InputStream, and to the location of
                    the file for a File or a Path.
  - `:charset`      charset of a File, Path or InputStream, as a string
                    ("ISO-8859-1") or a java.nio.charset.Charset. When
                    absent, it is detected from a byte-order mark or a
                    `<meta>` tag, with UTF-8 as the fallback. Ignored for
                    a String.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L8-L27">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/parse-fragment">`parse-fragment`</a><a name="io.github.papachan.montalivet/parse-fragment"></a>
``` clojure

(parse-fragment html)
(parse-fragment html opts)
```

Parses the `html` string as a fragment of a document body, and returns
  a jsoup Document whose body holds the fragment.

  Unlike [[[`parse`](#io.github.papachan.montalivet/parse)](#io.github.papachan.montalivet/parse)](#io.github.papachan.montalivet/parse), every element stays in the body: [[[`parse`](#io.github.papachan.montalivet/parse)](#io.github.papachan.montalivet/parse)](#io.github.papachan.montalivet/parse) moves elements
  such as `<title>` or `<meta>` to the head.

  Options: `:pretty-print`, `:escape-mode` and `:base-uri`, as in [[[`parse`](#io.github.papachan.montalivet/parse)](#io.github.papachan.montalivet/parse)](#io.github.papachan.montalivet/parse).
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L29-L38">Source</a></sub></p>

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
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L56-L65">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/select-one">`select-one`</a><a name="io.github.papachan.montalivet/select-one"></a>
``` clojure

(select-one doc query)
```

Returns the first element of `doc` that matches the CSS selector `query`,
  or nil when nothing matches.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L67-L71">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/text">`text`</a><a name="io.github.papachan.montalivet/text"></a>
``` clojure

(text el)
```

Returns the combined, whitespace-normalised text of `el` and its children.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L73-L76">Source</a></sub></p>
