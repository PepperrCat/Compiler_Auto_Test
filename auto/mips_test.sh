testfile=$1

if [ ! -d "./mipsout/" ];then
    mkdir mipsout
fi

sh mips.sh 
sed -i '1,2d' mipsoutput.txt
sed -i 's/\r//g' output.txt
sed -i 's/\r//g' mipsoutput.txt
echo"" >> mipsoutput.txt
echo"" >> output.txt
diff mipsoutput.txt output.txt -B > diff.txt
if test -s diff.txt; then
        cp diff.txt ./mipsout/${testfile}.out
fi
rm -rf *.txt