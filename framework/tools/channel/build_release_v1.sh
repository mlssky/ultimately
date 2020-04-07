#!/bin/bash

echo Start build releas apk======

if [ -d ./apks ]
  then
   rm -rf ./apks/*
   else
   mkdir apks
fi

pwdDir=`pwd`
scriptsRoot=${pwdDir}/releaseBuild
sdkDir=/Users/mengliwei/Library/Android/sdk
ndkDir=/Users/mengliwei/Library/Android/sdk/ndk-bundle

cd ./../
cp local.properties local.properties.back
rm local.properties
echo ndk.dir=${ndkDir} >>local.properties
echo sdk.dir=${sdkDir} >>local.properties
echo debugUpgrade= false >>local.properties
echo virtual= false >>local.properties
echo debug= false >>local.properties
echo useLocalSingCfg= true >>local.properties
echo storeFile=${scriptsRoot}/wt.keystore >>local.properties
echo storePassword=Wt20190715 >>local.properties
echo keyAlias=wt >>local.properties
echo keyPassword=Wt20190715 >>local.properties

rm -rf ./app/build/outputs/*
./gradlew assembleRelease

#release APK Path
apkFilePaths=` find ./app/build/outputs/apk/release  -name '*-release*.apk'`
for apkFilePath in $apkFilePaths
 do
   echo $apkFilePath

   apkFileName=${apkFilePath##*/}
   apkFileBaseName=${apkFileName%.apk}

   rm ${scriptsRoot}/tmp

   #v1 with 7zip
   java -jar ${scriptsRoot}/AndResGuard-cli-1.2.16.jar  $apkFilePath  -config ${scriptsRoot}/config.xml -out ${scriptsRoot}/tmp -7zip ${scriptsRoot}/7za -zipalign ${sdkDir}/build-tools/27.0.3/zipalign  -signatureType v1 -signature ${scriptsRoot}/wt.keystore Wt20190715 Wt20190715 wt
   apkNameCompressPath=`find ${scriptsRoot}/tmp -name "*_signed_7zip_aligned.apk"`

   if [ -d ${scriptsRoot}/out ]
    then
     echo 'out dir is exist'
   else
     mkdir ${scriptsRoot}/out
   fi

   cp ${apkNameCompressPath}  ${scriptsRoot}/out/${apkFileName}
   cp ./app/build/outputs/mapping/release/mapping.txt ${scriptsRoot}/out/${apkFileBaseName}.mapping.txt
   cp ${apkNameCompressPath}  ${scriptsRoot}/../apks/${apkFileName}

done

cp local.properties.back local.properties

rm local.properties.back
rm -rf ${scriptsRoot}/tmp

echo END build releas apk======



