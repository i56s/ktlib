## 更新说明
### v1.1.6
- 1.删除控件判断显示方法
- 2.控件单一点击事件函数增加间隔时间参数
- 3.增加 [SettingItemView](./doc/SettingItemView.md) 设置条纹栏控件
- 4.优化 SizeUtils 工具类
- 5.LibBaseFragment基类删除onCreateAfter方法，修改initData()、initEvent()执行时机

## 一、集成步骤

### 引入方式

- 如果你的项目 Gradle 配置是在 ``7.0`` 以下，需要在 ``build.gradle`` 文件中加入

```groovy
allprojects {
    repositories {
        // JitPack 远程仓库：https://jitpack.io
        maven { url 'https://jitpack.io' }
    }
}
```

- 如果你的 Gradle 配置是 ``7.0`` 及以上，则需要在 ``settings.gradle`` 文件中加入

```groovy
dependencyResolutionManagement {
    repositories {
        // JitPack 远程仓库：https://jitpack.io
        maven { url 'https://jitpack.io' }
    }
}
```

- 配置完远程仓库后，在项目 app 模块下的 ``build.gradle`` 文件中加入远程依赖 [![](https://jitpack.io/v/i56s/ktlib.svg)](https://jitpack.io/#i56s/ktlib)

```groovy
android {
    // 开启viewBinding
    buildFeatures {
        viewBinding true
    }
    // 支持 JDK 1.8
    compileOptions {
        targetCompatibility JavaVersion.VERSION_1_8
        sourceCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation 'com.github.i56s:ktlib:lastversion'
}
```

### 初始化类库

```
 I56sLib.init(application, isDebug, level = LogUtils.Level.DEBUG)
```

- application 当前应用对象
- isDebug 是否调试中
- level 默认的日志等级(使用LogUtils.log(tag, msg)时有效) 默认 LogUtils.Level.DEBUG

## 二、类介绍

### 1.主类和工具类

类 | 说明
--- | ---
I56sLib | 类库初始化类
TabPagerAdapter | TabLayout+ViewPager滑动适配器
BaseRecyclerAdapter | 列表适配器，封装添加头部/尾部控件
LibBaseActivity | activity的基类
LibBaseDialog | dialog基类
LibBaseFragment | 碎片页面，增加定时加载数据方法
ConfirmDialog | 确认弹框
LoadingDialog | 加载弹框
HtmlUtils | html转换工具类
LogUtils | 日志工具类
Md5Utils | MD5工具类
NetworkUtils | 网络状态工具类
SizeUtils | 尺寸工具类
SpUtils | SharedPreferences工具类
ToastUtils | 吐司工具类

### 2.控件类

类 | 说明
--- | ---
CstViewPager | 解决滑动冲突的 ViewPager
SwipeMenuLayout | Item侧滑删除菜单控件
XRecyclerView | 上拉加载，下拉刷新控件
MaterialRefreshLayout | 下拉刷新，上拉加载(布局)
BadgeView | 红点数字控件
LazyViewPager | 懒加载ViewPager
LoadingSwordView | 剑气加载控件
LoadingView | 菊花加载控件
MarqueeTextView | 跑马灯文本控件
PayPasswordView | 交易密码输入控件
IndicatorView | 指示器控件
[TitleView](./doc/TitleView.md) | 标题控件
[SwitchButton](./doc/SwitchButton.md) | 高仿ios开关按钮
[ProgressView](./doc/ProgressView.md) | 进度条
[SettingItemView](./doc/SettingItemView.md) | 设置条纹栏

## 三、其他
- ### [ASCII对照表（strings.xml 特殊字符）](./doc/SpecialChar.md)


## 待开发功能
- 1.浏览器控件
- 2.文档完善