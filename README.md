# Seiko

## 一.简介

Seiko是Mirai的又一个Android端实现。有关Mirai，请参阅[Mirai的Github仓库](https://github.com/mamoe/mirai)

它可以解决在Android上运行mirai-core程序的所有问题。通过Seiko可以简化在Android上使用Mirai的流程。

Seiko还提供了一个**伪代码引擎**以供光速建立一个自动回复程序。

> 由于本人能力有限，不能兼容Mirai-Console插件。
>
> 所以这个东西还是等我更牛逼的时候再做吧x(*有生之年*)

## 二.目前支持

- 登录(支持多BOT)，支持滑块验证和验证码验证
- 登录成功后，可以导出信息至电脑上，格式与MCL通用。
- 登录失败时，会直接删掉设备信息。最大程度避免'235'风控问题
- 插件系统(确切的说是加载器)
- 一套较为完善的伪代码引擎(~~当然bug还是很多~~)

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

## 五.伪代码

> 伪代码引擎已迁移到Mirai-Console并成功运行。可以前往[ConsolePlugin](ConsolePlugin)工程查看

[查看伪代码支持的格式和教程](DictionaryCore/README.md)

## 六.我的联系方式

[点击链接加入群聊【次元空间】](https://jq.qq.com/?_wv=1027&k=8vQZES3X)
