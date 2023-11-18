cp ./llvm_ir.txt ./target.ll 
clang -c target.ll
clang -o target target.o ../lib/sylib.o
timeout -k 5s 30 ./target < input.txt > irout.txt
if [ $? -eq 124 ];then
    echo "Error: program TLE!(maybe Endless Loop, Input Error etc.)" >> irout.txt 
fi

rm -rf target*