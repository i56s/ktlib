# TitleView 标题控件

### 标题控件继承自 ConstraintLayout 布局，可自用添加所需控件

## 效果图

![titleView](./imgs/title_view.png)

## 布局文件

- 基本使用

```xml
<com.i56s.ktlib.views.TitleView
        android:id="@+id/titleView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:tvAddStatusBarHeight="false"
        app:tvBackImg="@drawable/ic_baseline_keyboard_arrow_left_40"
        app:tvBgColor="@color/titleview_bg"
        app:tvHeight="@dimen/titleview_height"
        app:tvShowBack="true"
        app:tvTextColor="@color/titleview_title"
        app:tvTitle="这是一个标题"
        app:tvTitleSize="@dimen/titleview_title_size" />
```

- 添加控件

```xml
<com.i56s.ktlib.views.TitleView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="20dp"
        app:tvShowBack="false"
        app:tvTitle="标题">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginEnd="15dp"
            android:text="提交"
            android:textColor="@android:color/white"
            android:textSize="15sp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent" />
</com.i56s.ktlib.views.TitleView>
```

## 自定义属性说明

属性名 | 说明 | 默认值
--- | --- | ---
tvHeight | 标题高度 | dimen/titleview_height (50dp)
tvTitle | 标题文字 | -
tvTextColor | 标题文字颜色 | color/titleview_title (#FFFFFF)
tvTitleSize | 标题文字大小 | dimen/titleview_title_size (20sp)
tvShowBack | 是否显示回退按钮 | true
tvAddStatusBarHeight | 是否添加状态栏高度 | false
tvBgColor | 背景颜色 | color/titleview_bg (#4BAEFE)
tvBackImg | 返回按钮图 | drawable/ic_baseline_keyboard_arrow_left_40

### 以下属性可在相应的文件或文件夹中进行覆盖，覆盖后不必每次添加

属性名 | 所在文件 | 说明 | 默认值
--- | --- | --- | ---
ic_baseline_keyboard_arrow_left_40 | drawable(文件夹) | 返回按钮图 | -
titleview_height | dimens.xml | 标题高度 | 50dp
titleview_title_size | dimens.xml | 标题文字大小 | 20sp
titleview_title | color.xml | 标题文字颜色 | #FFFFFF
titleview_bg | color.xml | 背景颜色 | #4BAEFE

## 监听

```kotlin
//设置返回键点击事件
mBinding.titleView.setOnBackClickListener {
    LogUtils.d("标题", "返回点击")
    true//表示消费事件，点击不会关闭页面
}
//设置标题点击事件
mBinding.titleView.setOnTitleClickListener {
    LogUtils.d("标题", "标题点击")
}
```