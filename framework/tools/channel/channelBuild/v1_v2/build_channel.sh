#!/usr/bin/env bash

if [ -d ./../out ]
     then
       rm -rf ./../out/*.apk
     else
       mkdir ./../out
fi

#apkFilePath=` find ./  -name '*-release*.apk'`

apkFilePath=` find ./../../apks  -name '*-release*.apk'`

java -jar VasDolly.jar  put -c channelCfg.txt  $apkFilePath ./../out/