testfile=$1

if [ ! -d "./llvmout/" ];then
    mkdir llvmout
fi

sh irc.sh
sed -i 's/\r//g' output.txt
sed -i 's/\r//g' irout.txt
echo"" >> output.txt
echo"" >> irout.txt
diff irout.txt output.txt -B > diff.txt
if test -s diff.txt; then
        cp diff.txt ./llvmout/${testfile}.out
fi
rm -rf *.txt
