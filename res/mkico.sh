#!/bin/bash

for ico in `ls *.svg`
do
    filename=$(basename -- "$ico")
    inkscape -w 64 -h 34 $ico -o ../src/com/vitexsoftware/netbeans/modules/php/versionswitch/${filename%.*}.png
done
