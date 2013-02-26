(ns nio2.files
  (:import  [java.nio.file Files Path LinkOption]
            [java.nio.file.attribute FileAttribute])
  (:use nio2.io nio2.options))

(defn directory? [^Path p]
  "return true if p is a directory (no symbolic link)"
  (Files/isDirectory p (into-array LinkOption [])))

(defn regular-file? [^Path p]
  "return true if p is a regular file (no symbilic link)"
  (Files/isRegularFile p (into-array LinkOption [])))

(defn hidden? [^Path p]
  (Files/isHidden p))

(defn owner [^Path p & link-opts]
  (Files/getOwner p (link-options link-opts)))

(defn last-modified-time [^Path p & link-opts]
  (Files/getLastModifiedTime path (link-options link-opts)))

(defn parent [^Path p]
  (.getParent p))

(defn real-path [^Path p])
;;; fixme file-attributes ignored
(defn create-directories! [^Path p & file-attributes]
  (Files/createDirectories p (into-array FileAttribute [])))
