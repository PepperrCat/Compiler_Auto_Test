testfile=$1

if [ ! -d "./out/" ];then
    mkdir out
fi

java -jar compiler.jar 2>jre.txt
sh mips.sh > mipsoutput.txt
sed -i '1,2d' mipsoutput.txt
sed -i 's/\r//g' output.txt
sed -i 's/\r//g' mipsoutput.txt
echo"" >> mipsoutput.txt
echo"" >> output.txt
diff mipsoutput.txt output.txt -B > diff.txt
if test -s diff.txt; then
        cp diff.txt ./out/${testfile}.out
fi
