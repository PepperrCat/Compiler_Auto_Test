import os

test_dir_path = "testcase/"
testdirs = ["2022_A", "2021_B"]
import re

def replace_while_with_for(filename):
    with open(filename, 'r',errors='ignore') as file:
        content = file.read()

    # 使用正则表达式查找 while(cond) 的模式
    pattern = r'while\s*\((.*?)\)'
    matches = re.findall(pattern, content)

    for match in matches:
        # 构建替换的字符串
        replace_str = 'for(;{};)'.format(match)

        # 替换匹配项
        content = content.replace('while({})'.format(match), replace_str)

    with open(filename, 'w') as file:
        file.write(content)


for testdir in testdirs:
    for testfile in os.listdir(test_dir_path + testdir):
        testpath = os.path.join(test_dir_path, testdir, testfile)
        # if (testfile[0] == 'i'):
        #     cmd = "cp " + testpath + " input.txt"
        #     os.system(cmd)
        if (testfile[0] == 't'):  
            # cmd = "cp " + testpath + " testfile.txt"   
            # os.system(cmd)
            # cmd = "cp " + os.path.join(test_dir_path, testdir) + "/input" + testfile[8:-4] + ".txt" + " input.txt"   
            # os.system(cmd)
            # cmd = "cp " + os.path.join(test_dir_path, testdir) + "/output" + testfile[8:-4] + ".txt" + " output.txt"   
            # os.system(cmd)
            # test_name = testdir + "_" + testfile[0:-4]
            # print("Now walking " + test_name)
            # os.system("./" + execute_script + " " + test_name)
            replace_while_with_for(testpath)
        # if (testfile[0] == 'o'):  
        #     cmd = "cp " + testpath + " output.txt"   
        #     os.system(cmd)