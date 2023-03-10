## 一、首先需要初始化类库
```
 I56sLib.init(application, spName, isOut, level)
```

- application 当前应用对象
- spName SharedPreferences文件名
- isOut 是否输出日志(LogUtils工具类)
- level 默认的日志等级(使用LogUtils.log(tag, msg)时有效)

## 二、类介绍
### Adapter 适配器
类名 | 说明
--- | ---
TabPagerAdapter | TabLayout+ViewPager滑动适配器，使用 `addBean(String,Fragment)` 方法添加Fragment

### Base 基类
类名 | 说明
--- | ---
BaseRecyclerAdapter | 列表适配器，封装添加头部/尾部控件
LibBaseActivity | activity的基类
LibBaseDialog | dialog基类
LibBaseFragment | fragment基类

### Dialog 弹框
类名 | 说明
--- | ---
ConfirmDialog | 确认弹框
LoadingDialog | 加载弹框，有两种形状- TYPE_DEFAULT 和 TYPE_SWORD

### Utils 工具类
类名 | 说明
--- | ---
HtmlUtils | html转换工具类
LogUtils | 日志工具类
Md5Utils | 字符串转MD5 MD5加密/解密(以16进制形式)
NetworkUtils | 网络状态工具类
SizeUtils | 尺寸转换px dp sp互转
SpUtils | SharedPreferences工具类
ToastUtils | 吐司工具类

### Views 控件类
类名 | 说明
--- | ---
BadgeView | 红点数字
CstViewPager | 解决 滑动冲突的 ViewPager
LazyViewPager | 实现懒加载ViewPager
LoadingSwordView | 剑气加载
LoadingView | 菊花加载
MarqueeTextView | 文本跑马灯
PayPasswordView | 交易密码输入框
SwipeMenuLayout | Item侧滑删除菜单控件
TitleView | 标题控件
CircleProgressBar | 进度圆圈
MaterialLoaderView | 上拉或下拉的刷新控件
MaterialRefreshLayout | 上拉加载，下拉刷新控件（包裹）
XRecyclerView | 带RecyclerView的刷新控件
