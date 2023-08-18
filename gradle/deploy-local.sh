#!/bin/bash

./gradlew -p lib clean assembleRelease || { exit 1 ; }
./gradlew -p lib publishToMavenLocal || { exit 1 ; }

