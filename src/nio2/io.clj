(ns nio2.io
  (:import [java.nio.file Files FileSystem FileSystems Path]
           java.nio.charset.Charset
           java.net.URI
           [java.io InputStream OutputStream])
  (:require [clojure.java.io :as io])
  (:use nio2.options))

(defn filesystem [uri & options]
  "Create a Filesystem using uri and env created by optional keyword arguments.

For example: (filesystem \"jar:file:/home/juergen/zipfstest.zip\" :create  true)"
  ;; provide idiomatic clojure keyword arguments: filesystems always require string options
  (let [as-env-option (fn [o] (cond
                               (keyword? o) (name o)
                               (instance? Boolean o) (str o)
                               :else o))
        uri (cond
             (instance? String uri) (URI. uri)
             (not (instance? URI uri)) (throw (IllegalArgumentException. (str "Not a URI: " uri)))
             :else uri)
        env (apply hash-map (map as-env-option options))]
    (FileSystems/newFileSystem uri env)))

 
(defn ^Path path [path & more]
  "Return  a java.io.file.Path on default filesystem. Treat the first argument as first path element
and subsequent args as children relative to the parent."
  (.getPath (FileSystems/getDefault) path (into-array String more)))


(defn fs-path [fs path & more]
  "Return a java.io.file.Path on fs. Treat the first argument as first path element
and subsequent args as children relative to the parent."
  (.getPath fs path (into-array String more)))

(defn path-resolve [path other]
  "Resolve the other path against this path."
  ;; cannot resolve accross filesystem
  (let [other (if (= (.getFileSystem path) (.getFileSystem other))
                other
                (str other))]
    (.resolve path other)))

(defn- encoding [opts]
  (Charset/forName (or (:encoding opts)  "UTF-8")))

(extend-protocol io/IOFactory
  Path
  (make-reader [#^Path p opts]
    (Files/newBufferedReader p (encoding opts)))
  (make-input-stream [#^Path p opts]
    (Files/newInputStream p (open-options opts)))
  (make-output-stream [#^Path p opts]
    (Files/newOutputStream p (open-options opts)))
  (make-writer [#^Path p opts]
    (Files/newBufferedWriter p (encoding opts) (open-options opts))))

;;; workaround :io/do-copy is private
(defmethod @#'io/do-copy [InputStream Path] [#^InputStream input #^Path output opts]
  (Files/copy input output (copy-options opts)))

(defmethod @#'io/do-copy [InputStream Path] [#^Path input #^OutputStream output opts]
  (Files/copy input output (copy-options opts)))

(defmethod @#'io/do-copy [Path Path] [#^Path input #^Path output opts]
  (Files/copy input output (copy-options opts)))

