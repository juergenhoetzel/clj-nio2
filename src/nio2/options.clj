(ns nio2.options
  (:import [java.nio.file LinkOption OpenOption StandardOpenOption CopyOption StandardCopyOption]))

(def ^:private copy-option-mapping
  {:atomic-move StandardCopyOption/ATOMIC_MOVE
   :copy-attributes StandardCopyOption/COPY_ATTRIBUTES
   :replace-existing StandardCopyOption/REPLACE_EXISTING})

(defn copy-options [opts]
  (->> (map (fn [[opt v]]
              (and v (copy-option-mapping opt))) opts)
       (filter identity)
       (into-array CopyOption)))

(def ^:private option-mapping
  {:append StandardOpenOption/APPEND
   :create StandardOpenOption/CREATE
   :create-new StandardOpenOption/CREATE_NEW
   :delete-on-close StandardOpenOption/DELETE_ON_CLOSE
   :dync StandardOpenOption/DSYNC
   :read StandardOpenOption/READ
   :write StandardOpenOption/WRITE
   :sparse StandardOpenOption/SPARSE
   :sync StandardOpenOption/SYNC
   :truncate-existing StandardOpenOption/TRUNCATE_EXISTING})

(defn open-options [opts]
  (->> (map (fn [[opt v]]
              (and v (option-mapping opt))) opts)
       (filter identity)
       (into-array OpenOption)))

(def ^:private link-option-mapping
  {:no-follow-links LinkOption/NOFOLLOW_LINKS})

(defn link-options [opts]
  (->> (map (fn [[opt v]]
              (and v (link-option-mapping opt))) opts)
       (filter identity)
       (into-array LinkOption)))
