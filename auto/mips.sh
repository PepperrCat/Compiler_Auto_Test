cp ./mips.txt ./mips.asm
timeout -k 5s 30 java -jar mars.jar mips.asm < input.txt > mipsoutput.txt
if [ $? -eq 124 ];then
    echo "Error: program TLE!(maybe Endless Loop, Input Error etc.)" >> mipsoutput.txt 
fi
rm -rf mips.asm