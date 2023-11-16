import os

test_dir_path = "../testcase/"
testdirs = ["2022_A", "2022_B", "2022_C","2021_A", "2021_B", "2021_C" ]
# testdirs = ["public" ]
type = input("print auto type:(0 for llvm, 1 for mips, else for both)")
execute_scripts = ["mips_test.sh", "llvm_test.sh"]
if int(type) == 0:
    execute_scripts =  ["llvm_test.sh"]
    os.system("rm -rf llvmout/")
elif int(type) == 1:
    os.system("rm -rf mipsout/")
    execute_scripts =  ["mips_test.sh"]
else:
    os.system("rm -rf mipsout/")
    os.system("rm -rf llvmout/")

for testdir in testdirs:
    for testfile in os.listdir(test_dir_path + testdir):
        testpath = os.path.join(test_dir_path, testdir, testfile)
        # if (testfile[0] == 'i'):
        #     cmd = "cp " + testpath + " input.txt"
        #     os.system(cmd)
        if (testfile[0] == 't'):  
            cmd = "cp " + testpath + " testfile.txt"   
            os.system(cmd)
            cmd = "cp " + os.path.join(test_dir_path, testdir) + "/input" + testfile[8:-4] + ".txt" + " input.txt"   
            os.system(cmd)
            test_name = testdir + "_" + testfile[0:-4]
            print("Now walking " + test_name)
            os.system("java -jar compiler.jar 2>jre.txt >log.txt")
            cmd = "cp " + os.path.join(test_dir_path, testdir) + "/output" + testfile[8:-4] + ".txt" + " output.txt"   
            os.system(cmd)
            for execute_script in execute_scripts:
                os.system("./" + execute_script + " " + test_name)
           
        # if (testfile[0] == 'o'):  
        #     cmd = "cp " + testpath + " output.txt"   
        #     os.system(cmd)

os.system("rm -rf *.txt")