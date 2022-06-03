#!/bin/zsh

mkdir ../my-outputs > /dev/null 2>&1
mkdir ../my-outputs/bin > /dev/null 2>&1
touch ../my-outputs/bin/out.txt > /dev/null 2>&1
mkdir ../my-outputs/diff/ > /dev/null 2>&1
for folder in $(ls -d */);
do
    mkdir ../my-outputs/$folder > /dev/null 2>&1
    mkdir ../my-outputs/diff/$folder > /dev/null 2>&1
    cd $folder
    echo "Running $folder"

    for file in *.txt;
    do
        echo "Testing $(basename $file .txt)"
        cd ../../out/production/AirlineProfitMaximization-cmpe160/
        java project/executable/Main "../../../testcases/$folder/$file" "../../../my-outputs/$folder/$(basename $file .txt).txt"
        ../../../testcases/validity_checker "../../../testcases/$folder/$file" "../../../my-outputs/$folder/$(basename $file .txt).txt"  1 > "../../../my-outputs/diff/$folder/$(basename $file .txt).txt"
        cd - > /dev/null 2>&1

    done
    cd ..
done