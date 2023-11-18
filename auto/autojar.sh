src="../src"
cp -r $src .
echo "Manifest-Version: 1.0" > MANIFEST.MF
echo "Main-Class: Compiler" >> MANIFEST.MF

javac -d ./classes -encoding UTF-8 `find . -name "*.java"`  
cd ./classes
jar cmvf ../MANIFEST.MF ../compiler.jar .
cd ..

rm -rf ./src ./classes MANIFEST.MF

echo "compiler.jar has been made..."
