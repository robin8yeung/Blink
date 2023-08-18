#!/bin/bash

./gradlew -p blink-utils clean artifactoryPublish -Dusername=$1 -Dpassword=$2 -Dsnapshot=false || { exit 1 ; }
./gradlew -p blink-annotation clean artifactoryPublish -Dusername=$1 -Dpassword=$2 -Dsnapshot=false || { exit 1 ; }
./gradlew -p blink-ksp clean artifactoryPublish -Dusername=$1 -Dpassword=$2 -Dsnapshot=false || { exit 1 ; }
./gradlew -p blink-activity clean artifactoryPublish -Dusername=$1 -Dpassword=$2 -Dsnapshot=false || { exit 1 ; }
./gradlew -p blink-fragment clean artifactoryPublish -Dusername=$1 -Dpassword=$2 -Dsnapshot=false || { exit 1 ; }

