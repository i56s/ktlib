# SwitchButton 高仿ios开关按钮

## 效果图

![close](./imgs/switch_btn_close.png)
![open](./imgs/switch_btn_open.png)

## 布局文件

```xml
<com.i56s.ktlib.views.SwitchButton
        android:id="@+id/switchBtn"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:checked="true"
        app:closeCircleStrokeColor="#BFBFBF"
        app:closeColor="#DFDFDF"
        app:closeStrokeColor="#E3E3E3"
        app:disableColor="#BFBFBF"
        app:openCircleStrokeColor="#2CBCB7"
        app:openColor="#32D8D2"
        app:shadowCircleColor="#333333"
        app:shadowOpen="true" />
```
## 自定义属性说明

属性名 | 说明 | 默认值
--- | --- | ---
android:checked | 是否选中 | false
android:enabled | 是否启用 | true
shadowOpen | 是否打开圆圈阴影 | false
openColor | 开启状态背景色 | #32D8D2
openCircleStrokeColor | 开启状态圆圈描边色 | #2CBCB7
closeStrokeColor | 关闭状态描边色 | #E3E3E3
closeCircleStrokeColor | 关闭状态圆圈描边色 | #BFBFBF
closeColor | 关闭状态背景色 | #DFDFDF
disableColor | 禁用状态背景色 | #BFBFBF
shadowCircleColor | 圆圈阴影色 | #333333

## 监听

```kotlin
//是否选中监听
mBinding.switchBtn.setOnCheckedListener { button, checked ->
    Log.d("isChecked", checked.toString())
}
```