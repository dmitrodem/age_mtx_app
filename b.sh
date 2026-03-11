#!/bin/sh 
set -e
./gradlew assembleDebug
adb install ./app/build/outputs/apk/debug/app-debug.apk
adb shell 'am start -n org.demidrol.age_mtx/.MainActivity'
adb logcat -c
adb logcat -v brief,color 'MyApp:* *:S'
