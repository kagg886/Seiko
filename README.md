# Seiko

## 一.简介
Seiko是一个基于Mirai，并适用于Android端的一个QQ机器人软件  
由于本人能力有限，不能兼容Mirai-Console插件。  
有关Mirai，请参阅[Mirai的Github仓库](https://github.com/mamoe/mirai)

## 二.目前支持
- 登录(支持多BOT)，支持滑块验证和验证码验证
- 导出登录设备信息
- 简单的插件系统
- 简单的伪代码引擎

## 三.编写插件

1. 新建Android工程
2. 引入PluginAPI模块
3. 新建包，在包内新建一个类A，实现SeikoPlugin接口
4. 在onBotGoLine(long qq)中实现你的代码，使用`Bot.findInstance(long qq)`获取新登录的Bot。
5. 打包成apk文件，挪入`/sdcard/Android/data/com.kagg886.seiko/files/plugin`
6. 启动Seiko，前往插件列表查看是否有你所安装的插件

> 不推荐使用GlobalEventChannel。

# 四.插件示例
[前往PluginDemo工程查看](PluginDemo)

## 五.词库示例

本页面仍需完善，但已有了一些雏形
[前往SeikoDIC工程查看](SeikoDic/README.md)
## 六.我的联系方式

[点击链接加入群聊【次元空间】](https://jq.qq.com/?_wv=1027&k=8vQZES3X)
