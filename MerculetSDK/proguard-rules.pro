#指定不去忽略非公共的库类
-dontskipnonpubliclibraryclassmembers
#  不优化指定的文件
-dontoptimize
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#  通过指定数量的优化能执行
-optimizationpasses 5
-allowaccessmodification
#  不混淆指定的文件
#-dontobfuscate
-dontwarn
-ignorewarnings
#   混淆时不会产生形形色色的类名
-dontusemixedcaseclassnames

#不跳过library中的非public的类
#-dontskipnonpubliclibraryclasses

#混淆包路径
#-repackageclasses ''
-keepattributes *Annotation*,SourceFile,LineNumberTable,Signature
-renamesourcefileattribute SourceFile

#关闭预校验
-dontpreverify
-verbose
-dontnote com.android.vending.licensing.ILicensingService


# Gson specific classes
-keep class sun.misc.Unsafe {
    <fields>;
    <methods>;
}
# This is a configuration file for ProGuard.


# The support library contains references to newer platform versions.

# Dont warn about those in case this app is linking against an older

# platform version.  We know about them, and they are safe.

#对android.support包下的代码不警告，因为support包中有很多代码都是在高版本中使用的，如果我们的项目指定的版本比较低在打包时就会给予警告。不过support包中所有的代码都在版本兼容性上做足了判断，因此不用担心代码会出问题，所以直接忽略警告就可以了。
-dontwarn android.support.**

-keep public class * extends android.app.Activity

-keep public class * extends android.support.v7.app.AppCompatActivity

-keep public class * extends android.app.Application

-keep public class * extends android.app.Service

-keep public class * extends android.content.BroadcastReceiver

-keep public class * extends android.content.ContentProvider

-keep public class * extends android.app.backup.BackupAgentHelper

-keep public class * extends android.preference.Preference

-keep public class com.android.vending.licensing.ILicensingService

-keep interface android.support.v4.app.** { *; }
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context,android.util.AttributeSet);
    public <init>(android.content.Context,android.util.AttributeSet,int);
    public void set*(...);
    public void get*(...);
}

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.

-keep class * implements java.io.Serializable

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#不混淆任何包含native方法的类的类名以及native方法名。
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.

# see http://proguard.sourceforge.net/manual/examples.html#beans

#不混淆任何一个View中的setXxx()和getXxx()方法，因为属性动画需要有相应的setter和getter的方法实现，混淆了就无法工作了
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();

}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations

#不混淆枚举中的values()和valueOf()方法，枚举我用的非常少
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#不混淆R文件中的所有静态字段，我们都知道R文件是通过字段来记录每个资源的id的，字段名要是被混淆了，id也就找不着了
-keepclassmembers class **.R$* {
    public static <fields>;
}

#不混淆Parcelable实现类中的CREATOR字段，毫无疑问，CREATOR字段是绝对不能改变的，包括大小写都不能变，不然整个Parcelable工作机制都会失败
-keep class * extends android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * extends android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context,android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context,android.util.AttributeSet,int);
}


# Preserve static fields of inner classes of R classes that might be accessed
# through introspection.
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Also keep - Database drivers. Keep all implementations of java.sql.Driver.
-keep class * extends java.sql.Driver


########### MagicWindow Core Start###########

-keep class io.merculet.uav.sdk.domain.** {*;}
-keep class io.merculet.uav.sdk.MConfiguration {*;}
-keep class io.merculet.uav.sdk.manager.MWLifecycleCallbacks {*;}
-keep class io.merculet.uav.sdk.Session {<methods>;}
-keep class io.merculet.uav.sdk.RealTimeCallback {<methods>;}

-keep class io.merculet.uav.sdk.TrackAgent {<methods>;}
-keep interface io.merculet.uav.sdk.TrackAgent$TrackerInterface {
    <fields>;
    <methods>;
}
-keep class io.merculet.uav.sdk.queue.** {*;}
-keep class io.merculet.uav.sdk.config.Constant{*;}
-keep class io.merculet.uav.sdk.log.DebugLog{<methods>;}
-keep class io.merculet.uav.sdk.ShareHelper{<methods>;}

-keep class io.merculet.uav.sdk.http.JsonRequest{<methods>;}
-keep class io.merculet.uav.sdk.http.StringRequest{<methods>;}
-keep class io.merculet.uav.sdk.http.HttpFactory{<methods>;}
-keep class io.merculet.uav.sdk.http.JsonRequest{<methods>;}
-keep class io.merculet.uav.sdk.http.Request{*;}
-keep class io.merculet.uav.sdk.http.ResponseListener{*;}
-keep class io.merculet.uav.sdk.http.StringRequest{<methods>;}
-keep class io.merculet.uav.sdk.http.ImageLoader{*;}
-keep class io.merculet.uav.sdk.util.DeviceInfoUtils{<methods>;}
-keep class io.merculet.uav.sdk.util.HttpResponseUtils{<methods>;}
-keep class io.merculet.uav.sdk.util.JSONUtils{<methods>;}
-keep class io.merculet.uav.sdk.util.Preconditions{<methods>;}
-keep class io.merculet.uav.sdk.util.SPHelper{<methods>;}
-keep class io.merculet.uav.sdk.util.Util{<methods>;}
-keep class io.merculet.uav.sdk.util.DeviceInfoUtils{<methods>;}

-keep public class * extends android.view.ViewGroup {
    public <init>(android.content.Context);
    public <init>(android.content.Context,android.util.AttributeSet);
    public <init>(android.content.Context,android.util.AttributeSet,int);
    public void set*(...);
    public void get*(...);
    public static <fields>;
}

