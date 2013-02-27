(ns nio2.files
  "Mapping of [java.nio.file Files Path"
  (:import  [java.nio.file Files Path LinkOption]
            [java.nio.file.attribute FileAttribute])
  (:use nio2.io nio2.options))

(defn directory? [^Path p]
  "return true if p is a directory (no symbolic link)"
  (Files/isDirectory p (into-array LinkOption [])))

(defn regular-file? [^Path p]
  "return true if p is a regular file (no symbilic link)"
  (Files/isRegularFile p (into-array LinkOption [])))

(defn filesystem [^Path p]
  "Returns the file system of Path p"
  (.getFileSystem p))

(defn file-name
  "Returns a filename Path element of Path p.
   With additional arg i return Path element at index i. 
   With additional arg j return Seq of Path elements from i to j.

   Indices may be negative numbers, to start counting from the end.
   For example: (file-name (p 0 -1))"
  ([^Path p]
     (.getFileName p))
  ([^Path p i]
     (.getName p (if (neg? i) (+ (.getNameCount p) i) i)))
  ([^Path p i j]
     (let [j (if (neg? j) (+ (.getNameCount p) j) j)
           i (if (neg? i) (+ (.getNameCount p) i) i)]
       (for [k (range i j)] (.getName p k)))))

(defn hidden? [^Path p]
  (Files/isHidden p))

(defn owner [^Path p & link-opts]
  (Files/getOwner p (link-options link-opts)))

(defn last-modified-time [^Path p & link-opts]
  (Files/getLastModifiedTime p (link-options link-opts)))

(defn parent [^Path p]
  (.getParent p))

(defn real-path [^Path p & link-opts]
  (.toRealPath p (link-options link-opts)))

(defn resolve-path [^Path p1 ^Path p2]
  (.resolve p1 p2))

(defn relativize [^Path p1 ^Path p2]
  "Return a relative path between  p1 and p2"
  (.relativize p1 p2))

;;; fixme file-attributes ignored
(defn create-directories! [^Path p & file-attributes]
  (Files/createDirectories p (into-array FileAttribute [])))
