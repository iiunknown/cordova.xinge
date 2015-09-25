#腾讯信鸽Cordova插件
> 基于腾讯信鸽。

* Android Version: v2.39 updated at 2015-09-17
* iOS Version: V2.4.1 updated at 2015-09-22

> [腾讯信鸽](http://developer.xg.qq.com/index.php/Main_Page)

### 安装

```
$ cordova plugin add https://github.com/iiunknown/cordova.xinge.git
```

### 已知问题

* iOS中的CDVXinge.m不会自动增加到Xcode项目中的编译页面列表中，需要手工增加。
* iOS下启动代码中需要手工维护信鸽的ID和key。
* Android下UpdateApp.java中的引用命名空间需要根据项目名称手动调整。
* Android下需要修改AndroidManifest.xml中如下部分：

```
<!-- 【必须】 通知service，此选项有助于提高抵达率 -->
		    <service
		        android:name="com.tencent.android.tpush.rpc.XGRemoteService"
		        android:exported="true" >
		        <intent-filter>
		            <action android:name="应用包名.PUSH_ACTION" />
		        </intent-filter>
		    </service>
```

### 使用

