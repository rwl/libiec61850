#!/bin/sh

mkdir build

find src/ -name "*.java" > listFile.tmp

javac -d build @listFile.tmp

jar cfm genmodel.jar manifest.mf -C build/ com/

rm listFile.tmp
rm -r build
