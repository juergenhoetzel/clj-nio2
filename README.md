A NIO.2 library for Clojure
===========================

This package provides a lispy interface to NIO.2 (requires Java 7)

The most important NIO.2 types used in this packages are [Path][] and [FileSystem][]


Installation
============

Add the following to your `:dependencies`:

    [info.hoetzel/clj-nio2 "0.1.0"]


Quick Walk-Through
==================

Create Path objects
-------------------

On the default filesystem:

```clj
user> (require '[nio2.io :as io2])
nil
user> (io2/path "project.clj")
#<UnixPath project.clj>
```

On a different filesystem:

```clj
user> (io2/fs-path (io2/fs "jar:file:/home/juergen/zipfstest.zip" :create true) "project.clj")
#<ZipPath project.clj>
```

Use Path objects in clojure.java.io:
------------------------------------

`nio2.io` implements `Coercions` and `IOFactory` protocols.

```clj
user> (time (io/copy (io2/path "f:"  "isos" "archlinux-2013.02.01-dual.iso") (io2/path "f:" "isos" "temp.iso")))
"Elapsed time: 897.233123 msecs"
#<WindowsPath f:\isos\temp.iso>
user> (time (io/copy (io/file "f:\\isos\\archlinux-2013.02.01-dual.iso") (io/file "f:\\isos\\temp.iso")))
"Elapsed time: 5324.70911 msecs"
nil
```

Performance using NIO2 is much faster than the old API (File has to be copied in Userspace).


[Path]: http://docs.oracle.com/javase/7/docs/api/java/nio/file/Path.html
[FileSystem]: http://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html


Use dir-seq:
------------

Seq of Paths matching a glob:

```clj
user> (use 'nio2.dir-seq)
nil
user> (dir-seq-glob (path "") "*.clj")
(#<UnixPath project.clj>)
```

Seq of Paths matching a predicate:

```clj
user> (use 'nio2.files) ; for path related predicate functions
nil
user> (dir-seq-filter (path "") (partial is-owner? "juergen"))
(#<UnixPath pom.xml.asc> #<UnixPath src> #<UnixPath .git> #<UnixPath README.md> #<UnixPath target> #<UnixPath project.clj> #<UnixPath pom.xml>)
```


Handling Path events using watch-seq:
-------------------------------------

A Unix tail command implementation

```clj
(ns test.nio2.test.tail
  (:use clojure.java.io nio2.io nio2.watch nio2.files))

(defn tail [n p]
  "Print the last n lines of path p to stdout"
  (with-open [rdr (reader p)]
    (doseq [l (take-last n (line-seq rdr))]
      (println l))
    (doseq [e (watch-seq (parent (real-path p)) :modify)]
      (when (= (real-path (:path e)) (real-path p))
        (while (.ready rdr) (println (.readLine rdr)))))))
```

Watching a directory for changes:
---------------------------------

The below code will watch any events in the `\home\alex\tmp` directory. On any changes it receives an event, which it then prints it on the screen.

The watch-seq fn takes a path object, and event types as input. It returns a lazy-seq of events.

The event types that can be given to it can be any or all of - :create, :modify and :delete.

```clj
(:use [nio2.watch :only [watch-seq]]
      [nio2.io :only [path]])

(doseq [ev (watch-seq (path "/" "home" "alex" "tmp") :create :modify :delete)]
  (println [(ev :path) (ev :kind)]))

```
