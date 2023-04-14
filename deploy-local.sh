#!/bin/bash

./gradlew -p lib clean publishToMavenLocal || { exit 1 ; }

