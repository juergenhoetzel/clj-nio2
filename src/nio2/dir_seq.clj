(ns nio2.dir-seq
  (:use [nio2.files :only [directory?]])
  (:import [java.nio.file FileSystem FileSystems Files Path DirectoryStream DirectoryStream$Filter]))

;;; need to wrap, because the directory stream has to be closed
(defn- lazy-dir-stream-seq [^DirectoryStream dir-stream]
  (let [it (.iterator dir-stream)
        iter (fn thisfn []
               (if (.hasNext it)
                 (cons (.next it)
                       (lazy-seq (thisfn)))
                 (.close dir-stream)))]
    (lazy-seq (iter))))

(defn path-matcher
  "Return a predicate function that matches a path using syntax-and-pattern. Use the default filesystem
   if no filesystem is specified.

For example: \"glob:*.clj\" or \"regex:[0-9]*.txt\""
  ([^String syntax-and-pattern ^FileSystem fs]
     (let [matcher (.getPathMatcher fs syntax-and-pattern)]
       (fn [path]
         (.matches matcher path))))
  ([^String syntax-and-pattern]
     (path-matcher syntax-and-pattern (FileSystems/getDefault))))

(defn dir-seq [^Path p]
  "Return a lazy sequence of the Path entries in Path p"
  (-> (Files/newDirectoryStream p)
      (lazy-dir-stream-seq)))

(defn dir-seq-glob [^Path p ^String glob]
  "Return a lazy sequence of the Path entries in Path p  for which
   the globing parameter matches, e.g. \"*.clj\""
  (-> (Files/newDirectoryStream p glob)
      (lazy-dir-stream-seq)))

(defn dir-seq-filter [^Path p pred]
  "Returns a lazy sequence of the Path entries in Path p  for which 
  (pred p) returns true."
  (-> (Files/newDirectoryStream p (reify DirectoryStream$Filter
                                    (accept [_  p]
                                      (pred p))))
      (lazy-dir-stream-seq)))

(defn dir-seq-recursive [path]
  "Like clojure.core.file-seq, but fully lazy (directory contents). Returns a Seq of Path"
  (tree-seq
   directory?
   dir-seq
   path))

;;; filevisitor is too imperative for good mapping

