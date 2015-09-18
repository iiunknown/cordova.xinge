#腾讯信鸽Cordova插件
> 基于腾讯信鸽。

* Android Version: v2.39 updated at 2015-09-17
* iOS Version: V2.4.0 updated at 2015-07-20

> [腾讯信鸽](http://developer.xg.qq.com/index.php/Main_Page)

### 安装

```
$ cordova plugin add https://github.com/iiunknown/cordova.xinge.git
```

### 已知问题

* iOS中的CDVXinge.m不会自动增加到Xcode项目中的编译页面列表中，需要手工增加。
* iOS下启动代码中需要手工维护信鸽的ID和key。
* Android下UpdateApp.java中的引用命名空间需要根据项目名称手动调整。

### 使用

