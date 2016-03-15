#! /bin/bash
# compile.sh

javac -classpath "JOGL/jar/gluegen-rt.jar:JOGL/jar/jogl-all.jar:." -d bin src/**/**/*.java
