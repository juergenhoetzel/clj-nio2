(ns nio2.files
  (:import  [java.nio.file Files Path LinkOption]
            [java.nio.file.attribute FileAttribute])
  (:use nio2.io nio2.options))

(defn directory? [#^Path path]
  "return true if path is a directory (no symbolic link)"
  (Files/isDirectory path (into-array LinkOption [])))

(defn regular-file? [#^Path path]
  "return true if path is a regular file (no symbilic link)"
  (Files/isRegularFile path (into-array LinkOption [])))

(defn hidden? [#^Path path]
  (Files/isHidden path))

(defn owner [#^Path path & link-opts]
  (Files/getOwner path (link-options link-opts)))

(defn last-modified-time [#^Path path & link-opts]
  (Files/getLastModifiedTime path (link-options link-opts)))

(defn parent [#^Path path]
  (.getParent path))

;;; fixme file-attributes ignored
(defn create-directories! [#^Path path & file-attributes]
  (Files/createDirectories path (into-array FileAttribute [])))
