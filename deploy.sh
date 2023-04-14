#!/bin/bash

./gradlew -p lib clean artifactoryPublish -Dusername=$1 -Dpassword=$2 -Dsnapshot=false || { exit 1 ; }

