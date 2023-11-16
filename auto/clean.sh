# shopt -s extglob
# rm -rf !(*.sh|*.py|Makefile|mars.jar)
# shopt -u extglob

rm -rf *.txt|mips.asm|target*
rm -rf ./out/
rm -rf ./result/
echo "Everything is clean..."