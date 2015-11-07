(ns nio2.watch
  "Wrap watch events in a core.async channels"
  (:require [clojure.set :refer [map-invert]]
            [clojure.core.async :refer [thread >!! chan <!! <! go] :as async])
  (:import [java.nio.file Files FileSystem FileSystems Path StandardWatchEventKinds WatchEvent WatchEvent$Kind WatchEvent$Modifier]))


(def ^:private  event-mapping {:create StandardWatchEventKinds/ENTRY_CREATE
                               :delete StandardWatchEventKinds/ENTRY_DELETE
                               :modify StandardWatchEventKinds/ENTRY_MODIFY})

(defn get-watch-service
  "Returns a watch service given a Path"
  [path]
  (-> (.getFileSystem path)
      (.newWatchService)))

(defn- register
  "Registers a path with a watch service and the events to watch for."
  [path ws events]
  (.register path ws (into-array WatchEvent$Kind events)))

(defn- create-event-map
  "Returns a map of of event given an event object."
  [^WatchEvent e]
  {:kind (get (map-invert event-mapping)  (.kind e))
   :path (.context e)})

(defn- read-events
  "Takes a watch service as input, and returns a seq of events as output. This is a blocking fn, it will only return when events become avalaible."
  [ws]
  (let [key (.take ws)]
    (.reset key)
    (map create-event-map (.pollEvents key))))


;; Below is a non-variadic version of the concat, courtesy gfredericks (and tomjack) on IRC.
;; The below version is needed as apply concat and flatten both are variadic, and block for a second arg aslo to be realized before returning a value. Thus the first event always gets reported after the second event.
;; http://dev.clojure.org/jira/browse/CLJ-1218
(defn concats
  "A non variadic version of concat"
  [s]
  (lazy-seq (concat (first s) (concats (rest s)))))

(defn watch-chan [^Path path & events]
  "Returns a channel of {:event event-type :path path} corresponding to
  events generated on path.

  event-kind are keywords and may be of:
    :create
    :delete
    :modify"
  (let [c (chan)
        ws (get-watch-service path)
        events-to-watch (map event-mapping events)]
    (when (some nil? events-to-watch)
      (throw (IllegalArgumentException. (str "Not a valid (:create, :delete, :modify) event-kind: " events))))
    (register path ws events-to-watch)
    (async/thread (loop []
                    (if (reduce (fn [_ e]
                                  (>!! c e))
                                true
                                (read-events ws))
                      (recur)
                      (.close  ws))))
    c))
