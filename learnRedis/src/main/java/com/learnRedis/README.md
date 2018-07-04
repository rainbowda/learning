window上的64位redis下载地址https://github.com/MicrosoftArchive/redis/releases





linux 官网地址： https://redis.io/download

目前官网首页提供的版本是4.0.10

Redis版本列表 ：http://download.redis.io/releases/



安装

用linux命令wget

```linux
wget http://download.redis.io/releases/redis-4.0.10.tar.gz
```

或者从官网下载，然后上传文件到linux上

解压

```
tar xzf redis-4.0.10.tar.gz
```

进入redis目录，编译

```
cd redis-4.0.10
make
```

启动服务

```
src/redis-server
```

想要后台启动最后加个`&`

```
src/redis-server &
```

这样启动的话，系统已重启又要重新启动redis服务

我们可以加到系统启动里面，让它开机自启动



遇到的问题

Could not connect to Redis at 127.0.0.1:6379: Connection refused

找到redis目录的redis.conf 文件

修改`daemonize no` 为`daemonize yes `,也就是将no改为yes

