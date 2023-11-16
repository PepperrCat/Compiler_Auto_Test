

if [ $1 = "0" ];then
    sh irc.sh
    sed -i 's/\r//g' output.txt
    sed -i 's/\r//g' irout.txt
    echo"" >> output.txt
    echo"" >> irout.txt
    diff irout.txt output.txt -B > diff.txt
else
    sh mips.sh > mipsoutput.txt
    sed -i '1,2d' mipsoutput.txt
    sed -i 's/\r//g' output.txt
    sed -i 's/\r//g' mipsoutput.txt
    echo"" >> output.txt
    echo"" >> mipsoutput.txt
    diff mipsoutput.txt output.txt -B > diff.txt
fi

if [ ! -d "./result/" ];then
    mkdir result
else
    rm -rf ./result/*.txt
fi

cp *.txt result/
rm -rf *.txt