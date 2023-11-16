testfile=$1

if [ ! -d "./out/" ];then
    mkdir out
fi

java -jar compiler.jar 2>jre.txt
sh irc.sh
sed -i 's/\r//g' output.txt
sed -i 's/\r//g' irout.txt
echo"" >> output.txt
echo"" >> irout.txt
diff irout.txt output.txt -B > diff.txt
if test -s diff.txt; then
        cp diff.txt ./out/${testfile}.out
fi
