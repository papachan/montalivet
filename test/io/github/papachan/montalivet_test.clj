(ns io.github.papachan.montalivet-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [io.github.papachan.montalivet :as montalivet])
  (:import (java.io ByteArrayInputStream File)
           (java.nio.charset StandardCharsets)
           (java.nio.file Files)
           (org.jsoup.nodes Document)))

(deftest parse-test
  (testing "returns a jsoup Document"
    (is (instance? Document (montalivet/parse "<p>hi</p>"))))
  (testing "unknown escape mode is rejected"
    (is (thrown? clojure.lang.ExceptionInfo
                 (montalivet/parse "<p>hi</p>" {:escape-mode :nope})))))

(defn- temp-html
  "Writes `html` to a temporary file with `charset` and returns the File."
  ^File [^String html ^String charset]
  (let [f (File/createTempFile "montalivet" ".html")]
    (.deleteOnExit f)
    (Files/write (.toPath f) (.getBytes html charset)
                 ^"[Ljava.nio.file.OpenOption;" (make-array java.nio.file.OpenOption 0))
    f))

(deftest parse-input-test
  (let [html "<html><body><p>café</p><a href=\"/a\">l</a></body></html>"
        p    #(montalivet/text (montalivet/select-one % "p"))]
    (testing "a File, a Path and an InputStream give the same document as a String"
      (let [f (temp-html html "UTF-8")]
        (is (= "café" (p (montalivet/parse html))))
        (is (= "café" (p (montalivet/parse f))))
        (is (= "café" (p (montalivet/parse (.toPath f)))))
        (is (= "café" (p (montalivet/parse (ByteArrayInputStream. (.getBytes html "UTF-8"))))))))
    (testing ":charset decodes a File, a Path and an InputStream"
      (let [f (temp-html html "ISO-8859-1")]
        (is (= "café" (p (montalivet/parse f {:charset "ISO-8859-1"}))))
        (is (= "café" (p (montalivet/parse (.toPath f) {:charset StandardCharsets/ISO_8859_1}))))
        (is (= "café" (p (montalivet/parse (ByteArrayInputStream. (.getBytes html "ISO-8859-1"))
                                                {:charset "ISO-8859-1"}))))
        (is (not= "café" (p (montalivet/parse f))))))
    (testing "without :charset, the charset of a meta tag is used"
      (let [f (temp-html (str "<html><head><meta charset=\"ISO-8859-1\"></head>"
                              "<body><p>café</p></body></html>")
                         "ISO-8859-1")]
        (is (= "café" (p (montalivet/parse f))))))
    (testing "the output options apply to every input type"
      (is (= "<p>café</p><a href=\"/a\">l</a>"
             (montalivet/body-html (montalivet/parse (temp-html html "UTF-8") {:pretty-print false})))))
    (testing ":base-uri resolves relative links"
      (let [href #(.absUrl (montalivet/select-one % "a") "href")
            opts {:base-uri "https://example.com/"}]
        (is (= "" (href (montalivet/parse html))))
        (is (= "https://example.com/a" (href (montalivet/parse html opts))))
        (is (= "https://example.com/a" (href (montalivet/parse (temp-html html "UTF-8") opts))))
        (is (= "https://example.com/a" (href (montalivet/parse (.toPath (temp-html html "UTF-8")) opts))))
        (is (= "https://example.com/a"
               (href (montalivet/parse (ByteArrayInputStream. (.getBytes html "UTF-8")) opts))))))
    (testing "other input types and charsets are rejected"
      (is (thrown? clojure.lang.ExceptionInfo (montalivet/parse nil)))
      (is (thrown? clojure.lang.ExceptionInfo (montalivet/parse 42)))
      (is (thrown? clojure.lang.ExceptionInfo (montalivet/parse (.getBytes html "UTF-8"))))
      (is (thrown? clojure.lang.ExceptionInfo
                   (montalivet/parse (temp-html html "UTF-8") {:charset :utf-8}))))))

(deftest parse-fragment-test
  (testing "returns a Document whose body holds the fragment"
    (let [doc (montalivet/parse-fragment "<b>a</b><i>b</i>" {:pretty-print false})]
      (is (instance? Document doc))
      (is (= "<b>a</b><i>b</i>" (montalivet/body-html doc)))))
  (testing "head elements stay in the body, unlike with parse"
    (let [html "<title>t</title><p>x</p>"]
      (is (= html (montalivet/body-html (montalivet/parse-fragment html {:pretty-print false}))))
      (is (= "<p>x</p>" (montalivet/body-html (montalivet/parse html {:pretty-print false}))))))
  (testing "takes the :escape-mode and :base-uri options"
    (is (= "a&#xa0;b"
           (montalivet/body-html (montalivet/parse-fragment "a b" {:pretty-print false
                                                                       :escape-mode  :xhtml}))))
    (is (= "https://example.com/a"
           (-> (montalivet/parse-fragment "<a href=\"/a\">l</a>" {:base-uri "https://example.com/"})
               (montalivet/select-one "a")
               (.absUrl "href")))))
  (testing "only a string is accepted"
    (is (thrown? clojure.lang.ExceptionInfo (montalivet/parse-fragment nil)))
    (is (thrown? clojure.lang.ExceptionInfo
                 (montalivet/parse-fragment (File. "x.html"))))))

(deftest body-html-test
  (testing "without pretty print the markup is untouched"
    (is (= "<p>hi</p>"
           (-> (montalivet/parse "<p>hi</p>" {:pretty-print false})
               montalivet/body-html))))
  (testing "html fragments are wrapped into a body"
    (is (= "<b>a</b><i>b</i>"
           (-> (montalivet/parse "<b>a</b><i>b</i>" {:pretty-print false})
               montalivet/body-html)))))

(deftest escape-html-test
  (testing "special characters are escaped"
    (is (= "&lt;b&gt;a &amp; b&lt;/b&gt;"
           (montalivet/escape-html "<b>a & b</b>"))))
  (testing "plain text is unchanged"
    (is (= "hello" (montalivet/escape-html "hello"))))
  (testing "non-breaking space is escaped according to the escape mode"
    (is (= "a&nbsp;b" (montalivet/escape-html "a b" {:escape-mode :base})))
    (is (= "a&nbsp;b" (montalivet/escape-html "a b" {:escape-mode :extended})))
    (is (= "a&#xa0;b" (montalivet/escape-html "a b" {:escape-mode :xhtml}))))
  (testing "non-ASCII text is kept as is with the default UTF-8 charset"
    (is (= "café" (montalivet/escape-html "café" {:escape-mode :extended})))))

(deftest round-trip-test
  (testing "the original java snippet: parse, no pretty print, escape body"
    (let [doc (montalivet/parse "<p>1 < 2</p>"
                                {:pretty-print false :escape-mode :extended})]
      (is (= "<p>1 &lt; 2</p>" (montalivet/body-html doc)))
      (is (= "&lt;p&gt;1 &amp;lt; 2&lt;/p&gt;"
             (montalivet/escape-html (montalivet/body-html doc)))))))

(def page
  (str "<html><body>"
       "<div id=\"a\" class=\"x y\"><a href=\"/1\" title=\"one\">One</a><a href=\"/2\">Two</a></div>"
       "<p>Hi &amp; bye</p>"
       "</body></html>"))

(deftest select-test
  (let [doc (montalivet/parse page)]
    (testing "returns a vector of the matching elements in document order"
      (let [links (montalivet/select doc "a")]
        (is (vector? links))
        (is (= ["One" "Two"] (map montalivet/text links)))))
    (testing "supports CSS attribute selectors"
      (is (= ["/1" "/2"] (map #(montalivet/attr % :href) (montalivet/select doc "a[href]"))))
      (is (= ["One"] (map montalivet/text (montalivet/select doc "a[title=one]")))))
    (testing "an empty vector when nothing matches"
      (is (= [] (montalivet/select doc "table"))))
    (testing "can search inside an element"
      (let [div (montalivet/select-one doc "div")]
        (is (= ["One"] (map montalivet/text (montalivet/select div "a:first-child"))))
        (is (= ["/1"]
               (->> (montalivet/select div "a:first-child")
                    (map #(montalivet/attr % :href)))))))
    (testing "Expect an exception with an invalid or empty selector"
      (is (thrown? Exception (montalivet/select doc "a[")))
      (is (thrown? Exception (montalivet/select doc ""))))))

(deftest select-one-test
  (let [doc (montalivet/parse page)]
    (testing "returns the first match"
      (is (= "One" (montalivet/text (montalivet/select-one doc "a")))))
    (testing "returns nil when nothing matches"
      (is (nil? (montalivet/select-one doc "table"))))))

(deftest element-readers-test
  (let [doc (montalivet/parse page)
        a   (montalivet/select-one doc "a")
        div (montalivet/select-one doc "div")]
    (testing "text decodes entities"
      (is (= "Hi & bye" (montalivet/text (montalivet/select-one doc "p")))))
    (testing "attr accepts a string or a keyword and gives nil when absent"
      (is (= "one" (montalivet/attr a "title")))
      (is (= "one" (montalivet/attr a :title)))
      (is (nil? (montalivet/attr a :nope))))
    (testing "attrs returns a keyword map"
      (is (= {:id "a" :class "x y"} (montalivet/attrs div)))
      (is (= {} (montalivet/attrs (montalivet/select-one doc "p")))))
    (testing "outer-html includes the element's own tag"
      (is (= "<a href=\"/1\" title=\"one\">One</a>" (montalivet/outer-html a))))))

(deftest clean-test
  (testing "scripts and event handlers are removed with the default :basic safelist"
    (is (= "<p>Hi <b>there</b></p>"
           (montalivet/clean "<p onclick=\"x()\">Hi <script>alert(1)</script><b>there</b></p>"))))
  (testing "javascript: links lose their href"
    (is (= "<a rel=\"nofollow\">l</a>"
           (montalivet/clean "<a href=\"javascript:alert(1)\">l</a>"
                             {:base-uri "https://example.com/"}))))
  (testing ":none keeps only the text"
    (is (= "x" (montalivet/clean "<section class=\"c\"><b>x</b></section>" {:safelist :none}))))
  (testing "relative links are resolved against :base-uri"
    (is (= "<a href=\"https://example.com/a\">l</a>"
           (montalivet/clean "<a href=\"/a\">l</a>" {:base-uri "https://example.com/"}))))
  (testing "relative links can be preserved when a :base-uri is given"
    (is (= "<a href=\"/a\">l</a>"
           (montalivet/clean "<a href=\"/a\">l</a>"
                             {:base-uri "https://example.com/" :preserve-relative-links true}))))
  (testing "extra tags and attributes can be allowed"
    (is (= "<section class=\"c\"><b>x</b></section>"
           (montalivet/clean "<section class=\"c\"><b>x</b></section>"
                             {:add-tags [:section] :add-attributes {:section [:class]}})))
    (is (= "<p class=\"k\">x</p>"
           (montalivet/clean "<p class=\"k\">x</p>" {:add-attributes {:all [:class]}}))))
  (testing "tags and attributes can be removed"
    (is (= "x" (montalivet/clean "<b>x</b>" {:remove-tags [:b]})))
    (is (= "<a rel=\"nofollow\">l</a>"
           (montalivet/clean "<a href=\"https://example.com/\">l</a>"
                             {:remove-attributes {:a [:href]}}))))
  (testing "empty input gives an empty string"
    (is (= "" (montalivet/clean ""))))
  (testing "pretty-print is off by default and can be enabled"
    (is (= "<p>x</p>" (montalivet/clean "<p>x</p>")))
    (is (string? (montalivet/clean "<div><p>x</p></div>" {:safelist :relaxed :pretty-print true}))))
  (testing "an unknown safelist is rejected"
    (is (thrown? clojure.lang.ExceptionInfo
                 (montalivet/clean "<p>x</p>" {:safelist :nope})))))