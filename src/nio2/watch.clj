(ns nio2.watch
  "Wrap Watch events in a Clojure Seq"
  (:use [clojure.set :only [map-invert]])
  (:import [java.nio.file Files FileSystem FileSystems Path StandardWatchEventKinds WatchEvent WatchEvent$Kind WatchEvent$Modifier]))


(def ^:private  event-mapping {:create StandardWatchEventKinds/ENTRY_CREATE
                               :delete StandardWatchEventKinds/ENTRY_DELETE
                               :modify StandardWatchEventKinds/ENTRY_MODIFY})

(defn get-watch-service
  "Returns a watchable object given a Path"
  [path]
  (-> (.getFileSystem path)
      (.newWatchService)))

(defn- register
  [path ws events]
  (.register path ws (into-array WatchEvent$Kind events)))

(defn- create-event-map
  [^WatchEvent e]
  {:kind (get (map-invert event-mapping)  (.kind e))
   :path (.context e)})

(defn- read-events
  "Takes a watch service as input, and returns a seq of events as output. This is a blocking fn, it will only return when events become avalaible."
  [ws]
  (let [key (.take ws)]
    (.reset key)
    (map create-event-map (.pollEvents key))))

(defn watch-seq [^Path path & events]
  "Creates and returns a lazy sequence of {:event event-type :path path} corresponding to
  events generated on path.

  event-kind are keywords and may be of:
    :create
    :delete
    :modify"
  (let [ws (get-watch-service path)
        events-to-watch (map event-mapping events)]
    (when (some nil? events-to-watch)
      (throw (IllegalArgumentException. (str "Not a valid (:create, :delete, :modify) event-kind: " events))))
    (register path ws events-to-watch)
    (flatten (repeatedly #(read-events ws)))))


;;(concat (flatten (read-events ws)) (lazy-seq (read-events ws)))
