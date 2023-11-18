cp ../testfile.txt testfile.txt
cp ../input.txt input.txt
java -jar compiler.jar 2>RE.txt >log.txt

sh irc.sh
if [ ! -d "./test/" ];then
    mkdir test
else
    rm -rf ./test/*.txt
fi
cp *.txt ./test/
rm -rf ./test/input.txt
rm -rf ./test/testfile.txt
rm -rf *.txt