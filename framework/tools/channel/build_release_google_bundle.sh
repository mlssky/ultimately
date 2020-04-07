#!/bin/bash

echo Start build releas apk======

if [ -d ./bundles ]
  then
   rm -rf ./bundles/*
   else
   mkdir bundles
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
echo testNet= false >>local.properties
echo googlePkg= true >>local.properties
echo debug= false >>local.properties
echo useLocalSingCfg= true >>local.properties
echo storeFile=${scriptsRoot}/wp.keystore >>local.properties
echo storePassword=wp_20200107 >>local.properties
echo keyAlias=wp >>local.properties
echo keyPassword=wp_20200107 >>local.properties
#
rm -rf ./app/build/outputs/*
./gradlew clean bundleRelease

#release APK Path
apkFilePaths=` find ./app/build/outputs/bundle/release  -name '*-release*.aab'`
for apkFilePath in $apkFilePaths
 do
   echo $apkFilePath

   apkFileName=${apkFilePath##*/}
   apkFileBaseName=${apkFileName%.aab}

   rm -rf ${scriptsRoot}/tmp
   mkdir ${scriptsRoot}/tmp

   #v2 with 7zip
    java -jar ${scriptsRoot}/AabResGuard-0.1.4.jar \
    obfuscate-bundle \
    --bundle=$apkFilePath \
    --config=${scriptsRoot}/config_aab.xml  \
    --output=${scriptsRoot}/tmp/res_guard.aab \
    --merge-duplicated-res=true \
    --storeFile=${scriptsRoot}/wp.keystore \
    --storePassword=wp_20200107 \
    --keyAlias=wp  \
    --keyPassword=wp_20200107

    apkNameCompressPath=`find ${scriptsRoot}/tmp -name "*res_guard.aab"`

   if [ -d ${scriptsRoot}/out ]
    then
     echo 'out dir is exist'
   else
     mkdir ${scriptsRoot}/out
   fi

   cp ${apkNameCompressPath}  ${scriptsRoot}/out/${apkFileName}
   cp ./app/build/outputs/mapping/release/mapping.txt ${scriptsRoot}/out/${apkFileBaseName}.mapping.txt
   cp ${scriptsRoot}/tmp/resources-mapping.txt ${scriptsRoot}/out/${apkFileBaseName}.resources-mapping.txt
   cp ${apkNameCompressPath}  ${scriptsRoot}/../bundles/${apkFileName}
done


cp local.properties.back local.properties

rm local.properties.back
rm -rf ${scriptsRoot}/tmp

echo END build releas apk======



