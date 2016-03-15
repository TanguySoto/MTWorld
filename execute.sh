#! /bin/bash
# execute.sh

CLASSPATH="../JOGL/jar/gluegen-rt.jar:../JOGL/jar/jogl-all.jar:."

cd bin
java -classpath "$CLASSPATH" "MTWorld.applications.Main"
