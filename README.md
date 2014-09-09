![Image text](http://raw.github.com/revantis/VosDroid/master/android/res/drawable-xhdpi/ic_launcher.png)VosDroid
========

An android implementation of an old fasioned music game "Virtual Orchestra Studio (VOS) " which was originally developed by HanseulSoft.

Due to the lack of midi support on android platform, the game will unlikely supports note with sounds, the music will play along whether you tap or not.

========

对VOS这款游戏在安卓上的重建。

由于安卓平台缺乏对于midi单个音符的回放功能，最终的游戏很可能敲键会不带声音，背景音乐会一直播放。

========

目前的版本规划

(D=done,N=not-done)

D0.1 initial commit

D0.2 完成对vos文件的解析

D0.3 完成从vos到mid文件的转换（lots of thanks to LeffelMania's [android-midi-lib] (https://github.com/LeffelMania/android-midi-lib) )

D0.4 迁移到libgdx的界面上，释放0.4版本供测试人员进行测试。

D0.5 对原始版本的vos谱面可以进行播放同步预览，暂不支持tempo变换下的速度变化。释放0.5版本供预览。

D0.5.1 增加了长条的支持，界面现在可以通过进度条预览载入情况和播放进度。释放0.5.1版本供预览。

D0.5.1.1 为按下note的时设计了一个简单的特效。

N0.6 完成简单的游戏功能。
