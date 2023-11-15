import os

test_dir_path = "../testcase/"
testdirs = ["2022_A", "2022_B", "2022_C","2021_A", "2021_B", "2021_C","2023_A", "2023_B", "2023_C" ]
type = input("print auto type:(0 for llvm, else for mips)")
execute_script = "mips_test.sh"
if type == 0:
    execute_script =  "llvm_test.sh"

# os.system("rm -rf out/")
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
            cmd = "cp " + os.path.join(test_dir_path, testdir) + "/output" + testfile[8:-4] + ".txt" + " output.txt"   
            os.system(cmd)
            test_name = testdir + "_" + testfile[0:-4]
            print("Now walking " + test_name)
            os.system("./" + execute_script + " " + test_name)
           
        # if (testfile[0] == 'o'):  
        #     cmd = "cp " + testpath + " output.txt"   
        #     os.system(cmd)

if os.path.exists("./testfile.txt"):
    os.remove("./testfile.txt")
if os.path.exists("./input.txt"):
    os.remove("./input.txt")
if os.path.exists("./output.txt"):
    os.remove("./output.txt")
if os.path.exists("./mipsoutput.txt"):
    os.remove("./mipsoutput.txt")  
if os.path.exists("./irout.txt"):
    os.remove("./irout.txt")  
if os.path.exists("./jre.txt"):
    os.remove("./jre.txt")
if os.path.exists("./diff.txt"):
    os.remove("./diff.txt")
if os.path.exists("./mips.txt"):
    os.remove("./mips.txt")
if os.path.exists("./mips.asm"):
    os.remove("./mips.asm")
if os.path.exists("./target"):
    os.remove("./target")
if os.path.exists("./target.ll"):
    os.remove("./target.ll")
if os.path.exists("./target.o"):
    os.remove("./target.o")
if os.path.exists("./InstructionStatistics.txt"):
    os.remove("./InstructionStatistics.txt")