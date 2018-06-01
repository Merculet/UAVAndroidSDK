## Android集成文档

# 一. 集成准备

## 1.1导入SDK

导入SDK有以下三种方法，选择其中一种即可。

### 1.1.1  eclipse开发环境SDK集成

将需要的jar包拷贝到本地工程libs子目录下；在Eclipse中右键工程根目录，选择 Properties-> Java Build Path -> Libraries ，然后点击Add External JARs... 选择指向jar的路径，点击OK，即导入成功。（ADT17及以上不需要手动导入）

### 1.1.2 使用Android Studio导入SDK

Android Studio是谷歌推出了新的Android开发环境，本项目支持AndroidStudio的Gradle配置，如您使用AndroidStudio开发，请在您的App对应build.gradle文件中加入对统计SDK的依赖：

```groovy
dependencies{
	compile file(dir:'libs',include:[' MerculetSDK.jar'])
}
```

### 1.1.3  添加SDK在maven中心库的线上依赖

通过在Android Studio工程build.gradle配置脚本中添加maven线上依赖，导入最新版本组件化基础库和统计SDK。

在Gradle依赖中添加：

```groovy
dependencies {
	compile 'io.merculet:MerculetSDK:1.0.1'
}
```

如果无法正常集成请添加如下配置：

```groovy
allprojects {
    repositories {
            mavenCentral()     
    }
}
```

##

## 1.2添加权限(AndroidManifest.xml内)

```xml
<!-- 连接互联网Internet权限 -->
<uses-permission android:name="android.permission.INTERNET" />
<!-- 允许应用程序联网，以便向我们的服务器端发送数据。 -->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<!--判断程序是否在前台运行,必须 -->
<uses-permission android:name="android.permission.GET_TASKS" />
<!-- 检测手机基本状态 -->
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<!-- 缓存资源优先存入SDcard -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />  
```

## 1.3获取AppKey等信息

### 1.3.1后台获取Appkey等信息

登录后台管理（[http://merculet.cn/](http://merculet.cn/)）。进入“产品管理”菜单，填写相应内容创建应用，并获取应用的
appkey、account_key、account_secret的值。

### 1.3.2获取Token

基于上述三个数值，接入方需要调用签发Token的接口。



# 二．基本功能集成（必加项）

## 2.1初始化SDK


```java
MConfiguration.get().init(Application context)
```

在程序Application的onCreate函数中调用初始化接口

```java
MConfiguration.get().init(this)
        .setLogEnable(false)
        .setPageTrackWithFragment(true)
        .setChinaEnable(true);
```

初始化完成之后，需要向sdk设置token。此token需要调用获取用户凭证接口获得(https://mb-helpcenter.magicwindow.cn/doc/api)

```java
MConfiguration.get().init(app).setToken(token);
```

设置完token之后完成sdk的初始化工作。


此外，值得注意的是在用户注销登录时，需要调用

```java
TrackAgent.currentEvent().cancelUserProfile()
```

## 2.2 Session功能

注：Session为基础功能，必须集成，否则后台无法获取正确报告数据。

方法1：（Android-14之后才起作用。）

```java
MConfiguration.get().init(this);//内已调用Session.setAutoSession(this);
```


如果需要兼容 Android-14 （4.0）之前的版本，请用以下方法：

方法2 ：

在BaseActivity(父类activity)或者每个activity的相应函数里加入以下代码：

```java
@Override
protected void onPause() {
        Session.onPause(this);
        super.onPause();
    }

@Override
protected void onResume() {
        Session.onResume(this);
        super.onResume();
}
```


## 2.3自定义事件功能

自定义事件的注册（配置）包括事件id的注册和事件，id下参数信息的注册。

### 2.3.2统计场景举例

（1） 时间点事件

统计发生次数

```java
// eventId事件标识
TrackAgent.currentEvent().event("您自定义的event id");
```

统计带属性的行为触发次数

```java
// eventId事件标识
// properties事件参数的键值对
TrackAgent.currentEvent().event("您自定义的event id", Map<String,String>);
代码示例：：
HashMap<String,String> map = new HashMap<String,String>();
map.put("invitationCode","2098");
map.put("amount","300");
map.put("sign in","300"); //登录
TrackAgent.currentEvent().event ("您自定义的event id", map);
```

（2）时间段事件

代码示例：

```java
// eventId事件标识
// properties 事件参数的键值对
//调用位置：事件开始时调用
TrackAgent.currentEvent().event Start(String eventId );
//调用位置：事件结束后调用
TrackAgent.currentEvent().event (String eventId, Map<String,String> properties);
```



注意：

① 如果调用Process.kill或者System.exit之类的方法杀死进程，请务必在此之前调用Session.onKillProcess()方法，用来保证Session正确。

②如果需要混淆代码，为保证sdk 的正常使用，需要在proguard.cfg_加上如下配置：

```xml
-keep class io.merculet.** {*;}
-dontwarn io.merculet.**
-keep class io.merculet.** {*;}
-keep public enum
```



# 三．测试与调试

## 3.1测试前的准备

* 确认所需的权限都已经添加：INTERNET, READ*PHONE*STATE等

* 确认MW_APPID与WECHAT_APPID已经正确的写入Androidmanifest.xml

* 确认MWActivity在Androidmanifest.xml里正确声明

* 确认Session正确集成

* 确认测试手机(或者模拟器)已成功连入网络

## 3.2测试流程

使用普通测试流程，请先在程序入口初始化MConfiguration添加以下代码打开调试模式：

```java
config.setDebugMode( true );  
```

打开调试模式后，您可以在logcat中查看您的数据是否成功发送到merculet服务器，以及集成过程中的出错原因等。

| Log的tag  | 用途    | 结果           |
| -------- | ----- | ------------ |
| SDKDebug | 其他log | 系统调试的一些其他log |

注意：请使用普通测试流程，您的测试数据会与用户的真实使用数据同时处理，从而导致一定的数据污染。
