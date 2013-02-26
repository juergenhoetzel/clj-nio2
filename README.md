A NIO.2 library for clojure
===========================

This package provides a lispy interface to NIO.2 introduced with Java 7.

The most important NIO.2 types used in this packages are [Path][] and [FileSystem][]

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
user> (io2/fs-path (io2/filesystem "jar:file:/home/juergen/zipfstest.zip" :create true) "project.clj")
#<ZipPath project.clj>
```

Use Path objects in clojure.java.io:
------------------------------------

`nio2.io` implements `Coercions` and `IOFactory` protocols:

```clj
user> (require '[clojure.java.io :as io])
nil
user> (with-open [jarfs (io2/filesystem "jar:file:/home/juergen/zipfstest.zip" :create true)]
         (io/copy (io2/path "project.clj") (io2/fs-path jarfs "project.clj")))
#<ZipPath project.clj>
```


[Path]: http://docs.oracle.com/javase/7/docs/api/java/nio/file/Path.html
[FileSystem]: http://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html








