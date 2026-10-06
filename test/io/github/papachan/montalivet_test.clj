(ns io.github.papachan.montalivet-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [io.github.papachan.montalivet :as montalivet])
  (:import (org.jsoup.nodes Document)))

(deftest parse-test
  (testing "returns a jsoup Document"
    (is (instance? Document (montalivet/parse "<p>hi</p>"))))
  (testing "unknown escape mode is rejected"
    (is (thrown? clojure.lang.ExceptionInfo
                 (montalivet/parse "<p>hi</p>" {:escape-mode :nope})))))

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