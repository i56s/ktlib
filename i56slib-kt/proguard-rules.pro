# -allowaccessmodification
# -optimizationpasses 5
# -optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

# 不进行优化。优化可能会造成一些潜在风险，不能保证在所有版本的Dalvik上都正常运行
-dontoptimize

# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames

# 不跳过library中的非public的类
-dontskipnonpubliclibraryclasses

# 不跳过library中的非public的类成员
-dontskipnonpubliclibraryclassmembers

# 在处理时打印冗长的信息
-verbose

# 不做预校验，preverify是proguard的四个步骤之一，Android平台不需要preverify，去掉这一步能够加快混淆速度
-dontpreverify

# 保留异常表，以便编译器知道哪些异常可能被抛出
# 保留InnerClasses，否则外部无法引用内部类
# 保留Signature，否则无法访问泛型
-keepattributes Exceptions,SourceFile,LineNumberTable,EnclosingMethod,Signature,*Annotation*,InnerClasses,Deprecated




################################## 需要保留的公共部分 ##################################

# 保留四大组件，自定义的Application等等这些类不被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Appliction
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService


# 保留support包下的所有类及其内部类
-keep class android.support.** {*;}

# 保留继承的
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

# 保留R下面的资源
-keep class **.R$* {*;}

# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留在Activity中的方法参数是view的方法，
# 这样以来我们在layout中写的onClick就不会被影响
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 保留Serializable序列化的类不被混淆
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

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

#-----------WebView处理---------------
# 项目中没有使用到WebView忽略即可
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
    public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}

#-----------处理js交互---------------

-keep public class *{
    public *;
    protected *;
}

#-----------处理反射类---------------


#-----------处理实体类---------------
# 在开发的时候我们可以将所有的实体类放在一个包内，这样我们写一次混淆就行了。
#-keep public class com.upd.proguard.bean.** {
#    public void set*(***);
#    public *** get*();
#    public *** is*();
#}


#-----------处理第三方依赖库---------