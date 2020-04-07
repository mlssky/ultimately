#!/usr/bin/env bash

#V1 签名直接在写入文件
#acosch_渠道值
if [ -d ./../out/ ]
     then
       rm -rf ./../out/*.apk
     else
       mkdir ./../out
fi

for apkFile in ` ls ./../../apks/*.apk `
 do

   echo **********start $apkFile

   for line in `cat channelCfg.txt `
     do
        apkFileName=${apkFile##*/}
        apkFileBaseName=${apkFileName%.apk}
        cp $apkFile  $apkFile.back

        if [ -d META-INF ]
        then
           rm -rf ./META-INF/acosch_*
         else
           mkdir META-INF
        fi
        touch META-INF/acosch_$line
        zip -q -r $apkFile  ./META-INF/*

        ./../zipalign -f 4 $apkFile $apkFile.aligned
        mv $apkFile.aligned ./../out/$line-$apkFileBaseName.apk
        mv $apkFile.back $apkFile
        rm -rf ./META-INF
        echo **********end $apkFile with channel=$line
     done
 done




