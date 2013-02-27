A NIO.2 library for Clojure
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








