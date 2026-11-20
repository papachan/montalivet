# Table of contents
-  [`io.github.papachan.montalivet`](#io.github.papachan.montalivet)  - A Clojure library for parsing, sanitising and escaping HTML, built on jsoup.
    -  [`abs-url`](#io.github.papachan.montalivet/abs-url) - Returns the value of attribute <code>k</code> of <code>el</code> as an absolute URL, resolved against the base URI of the document.
    -  [`attr`](#io.github.papachan.montalivet/attr) - Returns the value of attribute <code>k</code> of <code>el</code>, or nil when it is absent.
    -  [`attrs`](#io.github.papachan.montalivet/attrs) - Returns the attributes of <code>el</code> as a map of keyword to string.
    -  [`base-uri`](#io.github.papachan.montalivet/base-uri) - Returns the base URI that <code>el</code> (a Document or any element) uses to resolve relative links in <code>abs-url</code>, or nil when it has none.
    -  [`body-html`](#io.github.papachan.montalivet/body-html) - Returns the inner HTML of the document body as a string.
    -  [`clean`](#io.github.papachan.montalivet/clean) - Removes everything from the <code>html</code> string that is not allowed by a safelist, so that untrusted markup can be embedded safely.
    -  [`escape-html`](#io.github.papachan.montalivet/escape-html) - Escapes <code>s</code> so it is safe to embed as HTML text.
    -  [`jsoup-version`](#io.github.papachan.montalivet/jsoup-version) - Returns the version of the jsoup library found on the classpath, as a string.
    -  [`outer-html`](#io.github.papachan.montalivet/outer-html) - Returns the markup of <code>el</code> itself, including its own tag.
    -  [`parse`](#io.github.papachan.montalivet/parse) - Parses <code>input</code> as a full HTML document and returns a jsoup Document.
    -  [`parse-fragment`](#io.github.papachan.montalivet/parse-fragment) - Parses the <code>html</code> string as a fragment of a document body, and returns a jsoup Document whose body holds the fragment.
    -  [`select`](#io.github.papachan.montalivet/select) - Returns a vector of the elements of <code>doc</code> that match the CSS selector <code>query</code>, in document order.
    -  [`select-one`](#io.github.papachan.montalivet/select-one) - Returns the first element of <code>doc</code> that matches the CSS selector <code>query</code>, or nil when nothing matches.
    -  [`text`](#io.github.papachan.montalivet/text) - Returns the combined, whitespace-normalised text of <code>el</code> and its children.
    -  [`unescape-html`](#io.github.papachan.montalivet/unescape-html) - Decodes the HTML entities in <code>s</code>: named (<code>&amp;amp;</code>, <code>&amp;copy;</code>), decimal (<code>&amp;#66;</code>) and hexadecimal (<code>&amp;#x41;</code>).
    -  [`valid?`](#io.github.papachan.montalivet/valid?) - Returns true when the <code>html</code> string contains nothing that <code>clean</code> would remove with the same safelist, so it can be used as it is.
    -  [`version`](#io.github.papachan.montalivet/version) - Returns the version of montalivet as a string, e.g.

-----
# <a name="io.github.papachan.montalivet">io.github.papachan.montalivet</a>


A Clojure library for parsing, sanitising and escaping HTML, built on jsoup.




## <a name="io.github.papachan.montalivet/abs-url">`abs-url`</a><a name="io.github.papachan.montalivet/abs-url"></a>
``` clojure

(abs-url el k)
```

Returns the value of attribute `k` of `el` as an absolute URL, resolved
  against the base URI of the document. Returns nil when the attribute is
  absent, or when it is relative and the document has no base URI, so it
  works with `if-let`, `some->` and `keep`. jsoup returns "" in both
  cases. `k` can be a string or a keyword.

  The URL is not checked: a `javascript:` link is returned as it is. Use
  [`clean`](#io.github.papachan.montalivet/clean) or [`valid?`](#io.github.papachan.montalivet/valid?) on untrusted markup.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L109-L119">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/attr">`attr`</a><a name="io.github.papachan.montalivet/attr"></a>
``` clojure

(attr el k)
```

Returns the value of attribute `k` of `el`, or nil when it is absent.
  `k` can be a string or a keyword.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L103-L107">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/attrs">`attrs`</a><a name="io.github.papachan.montalivet/attrs"></a>
``` clojure

(attrs el)
```

Returns the attributes of `el` as a map of keyword to string.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L133-L136">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/base-uri">`base-uri`</a><a name="io.github.papachan.montalivet/base-uri"></a>
``` clojure

(base-uri el)
```

Returns the base URI that `el` (a Document or any element) uses to
  resolve relative links in [`abs-url`](#io.github.papachan.montalivet/abs-url), or nil when it has none. jsoup
  returns "" then.

  It is the `:base-uri` given to [`parse`](#io.github.papachan.montalivet/parse) or [`parse-fragment`](#io.github.papachan.montalivet/parse-fragment), or for a File
  or a Path without that option, the absolute path of the file. A `<base
  href>` tag in the document replaces it, resolved against it when it is
  relative.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L121-L131">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/body-html">`body-html`</a><a name="io.github.papachan.montalivet/body-html"></a>
``` clojure

(body-html doc)
```

Returns the inner HTML of the document body as a string.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L53-L56">Source</a></sub></p>

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
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L143-L163">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/escape-html">`escape-html`</a><a name="io.github.papachan.montalivet/escape-html"></a>
``` clojure

(escape-html s)
(escape-html s opts)
```

Escapes `s` so it is safe to embed as HTML text.

  Takes the same `:escape-mode` and `:pretty-print` options as [`parse`](#io.github.papachan.montalivet/parse)
  (default :base).
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L58-L67">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/jsoup-version">`jsoup-version`</a><a name="io.github.papachan.montalivet/jsoup-version"></a>
``` clojure

(jsoup-version)
```

Returns the version of the jsoup library found on the classpath, as a
  string. It can differ from the version montalivet was built with when
  another dependency brings its own jsoup. The output of [`clean`](#io.github.papachan.montalivet/clean) and
  [`escape-html`](#io.github.papachan.montalivet/escape-html) can change between jsoup versions.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L13-L19">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/outer-html">`outer-html`</a><a name="io.github.papachan.montalivet/outer-html"></a>
``` clojure

(outer-html el)
```

Returns the markup of `el` itself, including its own tag.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L138-L141">Source</a></sub></p>

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
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L21-L40">Source</a></sub></p>

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
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L42-L51">Source</a></sub></p>

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
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L81-L90">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/select-one">`select-one`</a><a name="io.github.papachan.montalivet/select-one"></a>
``` clojure

(select-one doc query)
```

Returns the first element of `doc` that matches the CSS selector `query`,
  or nil when nothing matches.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L92-L96">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/text">`text`</a><a name="io.github.papachan.montalivet/text"></a>
``` clojure

(text el)
```

Returns the combined, whitespace-normalised text of `el` and its children.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L98-L101">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/unescape-html">`unescape-html`</a><a name="io.github.papachan.montalivet/unescape-html"></a>
``` clojure

(unescape-html s)
(unescape-html s opts)
```

Decodes the HTML entities in `s`: named (`&amp;`, `&copy;`), decimal
  (`&#66;`) and hexadecimal (`&#x41;`). Unknown entities are left as they
  are. It reverses [`escape-html`](#io.github.papachan.montalivet/escape-html).

  Options:
  - `:in-attribute` boolean, default false. Decodes `s` as an attribute
    value: a named entity without its `;` is then kept as it is when it is
    followed by `=` or by a letter or digit, as in the URL `a=1&copy=2`.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L69-L79">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/valid?">`valid?`</a><a name="io.github.papachan.montalivet/valid?"></a>
``` clojure

(valid? html)
(valid? html opts)
```

Returns true when the `html` string contains nothing that [[[`clean`](#io.github.papachan.montalivet/clean)](#io.github.papachan.montalivet/clean)](#io.github.papachan.montalivet/clean) would
  remove with the same safelist, so it can be used as it is. Use it to
  reject untrusted markup instead of cleaning it.

  Like [[[`clean`](#io.github.papachan.montalivet/clean)](#io.github.papachan.montalivet/clean)](#io.github.papachan.montalivet/clean), it checks a fragment of a body: a full document with
  `<html>` or `<head>` is never valid.

  Options: `:safelist`, `:add-tags`, `:remove-tags`, `:add-attributes`,
  `:remove-attributes` and `:preserve-relative-links`, as in [[[`clean`](#io.github.papachan.montalivet/clean)](#io.github.papachan.montalivet/clean)](#io.github.papachan.montalivet/clean). There
  is no base URI, so relative links are only valid with
  `:preserve-relative-links`.
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L165-L178">Source</a></sub></p>

## <a name="io.github.papachan.montalivet/version">`version`</a><a name="io.github.papachan.montalivet/version"></a>
``` clojure

(version)
```

Returns the version of montalivet as a string, e.g. "0.1.3".
<p><sub><a href="https://github.com/papachan/montalivet/blob/main/src\io\github\papachan\montalivet.clj#L8-L11">Source</a></sub></p>
