# shopt -s extglob
# rm -rf !(*.sh|*.py|Makefile|mars.jar)
# shopt -u extglob

rm -rf *.txt|mips.asm|target*
rm -rf ./llvmout/
rm -rf ./mipsout/
rm -rf ./result/
rm -rf ./test/
rm -rf compiler.jar
echo "Everything is clean..."