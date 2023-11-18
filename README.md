# ReadMe

将源码src文件夹（compiler.java入口）加入根目录后，运行以下命令即可：（linux上运行）

```makefile
make build # 编译并在 auto 中打包成 jar
make run   # 自动测试，输出位于auto/out目录下（错误才会输出）
make casetest  # 指定样例库中样例进行测试，样例和结果输出位于auto/result目录下
make llvmself || make mipsself  # 指定根目录下testfile样例进行测试，结果输出位于auto/test目录下
make clean 
make zip   # 打包，需要服务器安装zip
```
## Hint

报错`denied permission`则在根目录下使用`chmod -R 777 ./auto`即可

至少要求服务器安装jdk17以上版本，如：

```java
openjdk 17.0.8.1 2023-08-24
OpenJDK Runtime Environment (build 17.0.8.1+1-Ubuntu-0ubuntu122.04)
OpenJDK 64-Bit Server VM (build 17.0.8.1+1-Ubuntu-0ubuntu122.04, mixed mode, sharing)
```

至少要求clang llvm工具链版本为10.0以上，如：

```shell
Ubuntu clang version 14.0.0-1ubuntu1.1
Ubuntu LLVM version 14.0.0
```

