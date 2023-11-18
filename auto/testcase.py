import os
test_dir_path = "../testcase/"
year_type = input("print year type( 21, 22, 23, p ):")
if year_type == "p":
    testdir = "public"
else:
    case_type = input("print case type( A, B, C ):")
    testdir = "20" + year_type + "_" + case_type.upper()
case_num = input("print case number type( 1 ~ n ):")
testfile = "testfile" + case_num + ".txt"
inputtxt = "input" + case_num + ".txt"
output = "output" + case_num + ".txt"
testpath = os.path.join(test_dir_path, testdir, testfile)
inpath = os.path.join(test_dir_path, testdir, inputtxt)
outpath = os.path.join(test_dir_path, testdir, output)
cmd = "cp " + testpath + " testfile.txt"  
os.system(cmd)
cmd = "cp " + inpath + " input.txt"  
os.system(cmd)



test_type = input("print auto type(0 for llvm, else for mips):")
print(test_type)
if int(test_type) == 0:
    print("llvm:")
    os.system("java -jar compiler.jar 2>RE.txt >log.txt")
    cmd = "cp " + outpath + " output.txt"  
    os.system(cmd)
    os.system("./single_test.sh 0")
else:
    print("mips:")
    os.system("java -jar compiler.jar 2>jre.txt >log.txt")
    cmd = "cp " + outpath + " output.txt"  
    os.system(cmd)
    os.system("./single_test.sh 1")