(ns nio2.watch
  "Wrap Watch events in a Clojure Seq"
  (:use [clojure.set :only [map-invert]])
  (:import [java.nio.file Files FileSystem FileSystems Path StandardWatchEventKinds WatchEvent WatchEvent$Kind WatchEvent$Modifier]))


(def ^:private  event-mapping {:create StandardWatchEventKinds/ENTRY_CREATE
                               :delete StandardWatchEventKinds/ENTRY_DELETE
                               :modify StandardWatchEventKinds/ENTRY_MODIFY})

(defn watch-seq [^Path path event-kind & event-kinds]
  "Creates and returns a lazy sequence of {:event event-type :path path} corresponding to
  events generated on path.

  event-kind are keywords and may be of:
    :create
    :delete
    :modify"
  (let [ws (-> (.getFileSystem path)
               (.newWatchService))
        event-kinds (map event-mapping (cons event-kind event-kinds))
        iter (fn thisfn []
               (let [key (.take ws)
                     events (.pollEvents key)]
                 (let [events  (map (fn [^WatchEvent e]
                                      {:kind (get (map-invert event-mapping)  (.kind e))
                                       :path (.context e)}) events)]
                   (.reset key)
                   (lazy-seq (concat  events (lazy-seq (thisfn)))))))]
    (when (some nil? event-kinds)
      (throw (IllegalArgumentException. (str "Not a valid (:create, :delete, :modify) event-kind: " event-kind))))
    (.register path ws (into-array WatchEvent$Kind event-kinds))
    (iter)))
