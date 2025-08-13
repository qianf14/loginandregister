# CircleProgressBar 自定义视图

## 简介
CircleProgressBar 是一个自定义的Android视图组件，用于显示环形进度条，具有以下特性：
- 环形进度条显示（0-100%）
- 颜色渐变效果（从绿色到红色）
- 支持长按弹出菜单调整进度
- 可通过SeekBar调整进度值或重置进度
- 支持XML自定义属性配置

## 功能特性
1. **环形进度显示**：以圆形方式显示进度，范围0-100%
2. **颜色渐变**：进度条颜色从绿色（0%）渐变到红色（100%）
3. **交互功能**：
   - 长按进度条弹出菜单
   - 菜单选项包括调整进度和重置进度
   - 通过SeekBar调整进度值
4. **可定制属性**：
   - progress：初始进度值
   - strokeWidth：环形线宽
   - backgroundColor：背景环颜色
   - startColor：起始颜色（0%进度）
   - endColor：结束颜色（100%进度）
   - showText：是否显示进度文本
   - progressTextSuffix：进度文本后缀

## 使用方法

### 1. 在XML布局中使用
```xml
<com.example.loginandregister.view.CircleProgressBar
    android:id="@+id/circleProgressBar"
    android:layout_width="200dp"
    android:layout_height="200dp"
    app:progress="75"
    app:strokeWidth="20dp"
    app:backgroundColor="#E0E0E0"
    app:startColor="#00FF00"
    app:endColor="#FF0000"
    app:showText="true"
    app:progressTextSuffix="%" />
```

### 2. 在Activity中使用
```java
CircleProgressBar circleProgressBar = findViewById(R.id.circleProgressBar);

// 设置进度
circleProgressBar.setProgress(50);

// 重置进度
circleProgressBar.resetProgress();

// 设置进度变化监听器
circleProgressBar.setOnProgressChangedListener(new CircleProgressBar.OnProgressChangedListener() {
    @Override
    public void onProgressChanged(float progress) {
        // 处理进度变化
    }
});
```

## 文件结构
```
app/src/main/java/com/example/loginandregister/view/
  └── CircleProgressBar.java

app/src/main/res/values/
  └── attrs.xml

app/src/main/res/layout/
  ├── activity_circle_progress_demo.xml
  └── dialog_progress_adjust.xml

app/src/main/res/menu/
  └── menu_circle_progress.xml

app/src/main/java/com/example/loginandregister/ui/
  └── CircleProgressDemoActivity.java
```

## 自定义属性说明
| 属性名 | 格式 | 说明 |
|--------|------|------|
| progress | float | 初始进度值（默认0） |
| maxProgress | float | 最大进度值（默认100） |
| minProgress | float | 最小进度值（默认0） |
| strokeWidth | dimension | 环形线宽（默认20dp） |
| backgroundColor | color | 背景环颜色（默认#E0E0E0） |
| startColor | color | 起始颜色（默认绿色） |
| endColor | color | 结束颜色（默认红色） |
| showText | boolean | 是否显示进度文本（默认true） |
| progressTextSuffix | string | 进度文本后缀（默认"%"） |

## 注意事项
1. 确保在AndroidManifest.xml中注册使用CircleProgressBar的Activity
2. 自定义属性需要在XML中使用`app`命名空间
3. 长按功能需要在代码中设置`OnContextMenuListener`

## 示例演示
运行`CircleProgressDemoActivity`可以查看CircleProgressBar的所有功能演示。
