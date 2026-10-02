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
