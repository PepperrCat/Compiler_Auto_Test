# ReadMe

将源码传至src内运行以下命令即可：（linux上运行）

```mak
make build # 编译并在 auto 中打包成 jar	
make run   # 自动测试，输出位于auto/out目录下（错误才会输出）
make clean 
make zip   # 打包，需要安装zip
```

.sh可能权限缺失报错用以下命令可以解决

```shell
chmod -R 777 auto  
```

项目借鉴了[Thysrael](https://github.com/Thysrael)大佬，进行了小小的本地化改动，仅供学习使用。
