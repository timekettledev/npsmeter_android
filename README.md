# NPSMeter Android 版 SDK 集成说明

本SDK通过Kotlin进行开发，可以根据自身的开发语言通过Java或Kotlin进行集成使用

# 版本  1.19.3

## 集成SDK

#### 1. 添加maven仓库地址
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

```

#### 2. 引入最新依赖
```gradle
implementation 'com.github.timekettledev:npsmeter_android:1.2'
```

## 调起问卷

```
NpsMeter.show(
                传递问卷id字符串,
                传递userid，可为null,
                传递username，可为null,
                传递remark，可为null,
                supportFragmentManager,
                baseContext,
                300, //如果ui样式是显示在底部，需要与屏幕底部的间距
                {
                    // 展示问卷成功回调
                },
                { type ->
                    when (type) {
                        NPSCloseType.Finish -> {
                            //完成问卷
                        }
                        NPSCloseType.User -> {
                            // 用户关闭
                        }
                        NPSCloseType.OtherError -> {
                            //其他异常
                        }
                        NPSCloseType.DownFail -> {
                            // 下载失败
                        }
                        NPSCloseType.MinFatigue -> {
                            // 距离上次弹出问卷时间过短
                        }
                        NPSCloseType.FirstDay -> {
                            // 距离首次下载配置时间过短
                        }
                        NPSCloseType.HaveShowForId -> {
                            // 已经显示过该问卷
                        }
                        NPSCloseType.RequestAnswerError -> {
                            // 回答问卷请求出错
                        }
                    }
                })
```

# 说明
使用中，请勿对SDK做混淆（混淆后可能无法正常调用显示）
