# ScaleView 选择器:包括弧形刻度尺选择器和直线型刻度尺选择器
## 预览 弧形刻度尺选择器
<img src="/preview/demo.gif"/>






## 预览 直尺刻度尺选择器
<img src="/preview/demo_scale_line.gif"/>






## 使用方法:
## Step 1. Add the JitPack repository to your build file 
## Add it in your root build.gradle at the end of repositories:
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```


## Step 2. Add the dependency

```
 	dependencies {
        	        implementation 'com.github.yhongm:ArcScaleView:-40ab7f3b87-1'
        	}
```

 
## 1，布局文件添加以下属性
 ### app:shape="arc" arc为弧形，line为直尺形
 ### app:arcLineColor="#ff0000" 弧线颜色
 ### app:drawLineSpace="1" 刻度线间距
 ### app:drawTextSpace="5" 刻度值间隔
 ### app:everyScaleValue="1" 滑动像素与刻度值的比例
 ### app:indicatorColor="#1cffaf" 中间指示器颜色
 ### app:scaleLineColor="#0000ff" 刻度线颜色
 ### app:scaleMin="200" 选择器最小值
 ### app:scaleNum="30" 刻度数量
 ### app:scaleSpace="1" 刻度间距
 ### app:scaleTextColor="#0000ff" 刻度值颜色
 ### app:scaleUnit="单位" 刻度单位
 ### app:selectTextColor="#111111" 选中值颜色

## 2，实现setSelectScaleListener监听刻度值变化
