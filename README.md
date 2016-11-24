# -SwipeCaptcha
Swipe captcha of Android platform。
Android 平台的滑动验证码。

**在 Android端app上，自定义View，仿一个斗鱼web端滑动验证码**。
博文：http://gold.xitu.io/post/5835660261ff4b0061f28f54

![我们的Demo,Ac娘镇楼](http://ac-mhke0kuv.clouddn.com/7fcb58653e358b9ec003.gif)

# Usage 用法：
Step 1. Add the JitPack repository to your build file
在项目根build.gradle文件中增加JitPack仓库依赖。
```
    allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
```
Step 2. Add the dependency
```
    dependencies {
	        compile 'com.github.mcxtzhang:SwipeCaptcha:V1.0.0'
	}
```


Step 3. 
```
    <com.mcxtzhang.captchalib.SwipeCaptchaView
        android:id="@+id/swipeCaptchaView"
        android:layout_width="300dp"
        android:layout_height="200dp"
        android:layout_centerHorizontal="true"
        android:scaleType="centerCrop"
        tools:src="@drawable/pic11"
        app:captchaHeight="30dp"
        app:captchaWidth="30dp"/>

    <SeekBar
        android:id="@+id/dragBar"
        android:layout_width="320dp"
        android:layout_height="60dp"
        android:layout_below="@id/swipeCaptchaView"
        android:layout_centerHorizontal="true"
        android:layout_marginTop="30dp"
        android:progressDrawable="@drawable/dragbg"
        android:thumb="@drawable/thumb_bg"/>
```




那么本控件包含不仅包含以下功能：
* 随机区域**起点(左上角x,y)**生成一个验证码阴影。
* 验证码拼图 **凹凸图形**会随机变换。
* 验证码区域**宽高**可自定义。
* **抠图**验证码区域，绘制一个用于联动滑动的验证码滑块。
* 验证失败，会闪烁几下然后回到原点。
* 验证成功，会有白光扫过的动画。

分解一下验证码核心实现思路：
*  控件继承自ImageView。理由：
1 如果放在项目中用，验证码图片希望可以是接口返回。ImageView以及其子类支持花式加载图片。
2 继承自ImageView，绘制图片本身不用我们干预，也不用我们操心scaleType，节省很多工作。
* 在`onSizeChanged()`方法中生成 和 控件宽高相关的属性值：
1 初始化时随机生成验证码区域起点
2 生成验证码区域Path
3 生成滑块Bitmap
* `onDraw()`时，依次绘制：
1 验证码阴影
2 滑块

# to do list,待完善
* abstract dragbar(seekbar) interface
* SwipeCaptcha inside to hold the interface to do something

* 抽象拖动条接口
* SwipeCaptcha内部要持有这个接口 做一些事情
