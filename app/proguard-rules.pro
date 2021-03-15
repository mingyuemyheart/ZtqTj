
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify
-ignorewarnings
-dontskipnonpubliclibraryclasses
-verbose


-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
#保留native的方法的方法名和包含native方法的类的类名不变
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {}

-keep class com.pcs.knowing_weather.view.myview.*{}
# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity { #保留继承于Activity的类中以View为参数，返回值是void的方法的方法名
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable { #保留实现了Parcelable接口的类的类名以及Parcelable$Createor内部类的类名
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class **.R$* { #保留R$*类中静态字段的字段名
    public static <fields>;
}

#-libraryjars libs/xUtils-2.6.14.jar
 -keep class com.lidroid.xutils.* {*; }

-keep class packagename.** {*;}
-dontwarn com.jeremyfeinstein.slidingmenu.lib.*
-keep class com.jeremyfeinstein.slidingmenu.lib** { *;}
-dontwarn com.jeremyfeinstein.slidingmenu.lib.app*
-keep class com.jeremyfeinstein.slidingmenu.lib.app** { *;}
-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }

-dontwarn org.apache.http.**
-keep class org.apache.http.** { *;}

-dontwarn com.pcs.knowing_weather.control.tool.shareUtil
-keep class com.pcs.knowing_weather.control.tool.shareUtil{ *;}

#分享
-dontusemixedcaseclassnames
-dontshrink
-dontoptimize
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-dontwarn com.facebook.**
-keep public class javax.**
-keep public class android.webkit.**
-dontwarn android.support.v4.**
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**

-keep public class com.umeng.socialize.* {*;}


-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**
-keep class com.umeng.socialize.handler.**
-keep class com.umeng.socialize.handler.*
-keep class com.umeng.weixin.handler.**
-keep class com.umeng.weixin.handler.*
-keep class com.umeng.qq.handler.**
-keep class com.umeng.qq.handler.*
-keep class UMMoreHandler{*;}
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements   com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
-keep class im.yixin.sdk.api.YXMessage {*;}
-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}
-keep class com.tencent.mm.sdk.** {
 *;
}
-keep class com.tencent.** {*;}
-dontwarn com.tencent.**
-keep public class com.umeng.com.umeng.soexample.R$*{
public static final int *;
}
-keep public class com.linkedin.android.mobilesdk.R$*{
public static final int *;
    }
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}
-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}

-keep class com.sina.** {*;}
-dontwarn com.sina.**
-keep class  com.alipay.share.sdk.** {
   *;
}
-keepnames class * implements android.os.Parcelable {
public static final ** CREATOR;
}

-keep class com.linkedin.** { *; }
-keepattributes Signature


#高德相关混淆文件
#如果有其它包有warning，在报出warning的包加入下面类似的-dontwarn 报名
-dontwarn com.amap.api.**
-dontwarn com.aps.**
#3D 地图
-keep class com.amap.api.maps.**{*;}
-keep class com.autonavi.**{*;}
-keep class com.amap.api.trace.**{*;}
#Location
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}
#Service
-keep class com.amap.api.services.**{*;}

#信鸽服务
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep class com.tencent.android.tpush.** {* ;}
-keep class com.tencent.mid.** {* ;}
-keep class com.qq.taf.jce.** {*;}
-keep class com.tencent.xinge.** {* ;}
-keep class com.tencent.wup.** {* ;}

#华为通道
-ignorewarning
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}
-keep class com.huawei.android.hms.agent.**{*;}

#MAA
-keep class com.mato.** { *; }
-dontwarn com.mato.**
-keepattributes Exceptions, Signature, InnerClasses, EnclosingMethod

-keep public class com.tencent.bugly.**{*;}

-assumenosideeffects class android.util.Log{
		public static *** d(...);
		public static *** i(...);
		public static *** v(...);
	}
-keep public class com.pcs.knowing_weather.R$*{
	public static final int *;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    }


#保留annotation， 例如 @JavascriptInterface 等 annotation
-keepattributes *Annotation*

#保留跟 javascript相关的属性
-keepattributes JavascriptInterface

#保留JavascriptInterface中的方法
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

#这个根据自己的project来设置，这个类用来与js交互，所以这个类中的 字段 ，方法， 等尽量保持
-keepclassmembers public class com.pcs.ztq.view.activity.web.webview.JsInterfaceWebView{
   <fields>;
   <methods>;
   public *;
   private *;
}

#这个根据自己的project来设置，这个类用来与js交互，所以这个类中的 字段 ，方法， 等尽量保持
-keepclassmembers public class com.pcs.ztq.view.activity.web.JsWeatherDayCommitInterface{
   <fields>;
   <methods>;
   public *;
   private *;
}
##这个类 必须保留，这个类在WVJBWebViewClient中传递数据，如果被混淆 会导致一些callback无法调用
#-keep class com.packgename.custom.WVJBWebViewClient$WVJBMessage(WVJBMessage 交互bean)
##类中成员的变量名也不能混淆，这些变量名被作为json中的字段，不能改变。
#-keepclassmembers class com.packgename.custom.WVJBWebViewClient$WVJBMessage{
#    <fields>;
#}
#讯飞语音
-keep class com.iflytek.**{*;}
-keepattributes Signature