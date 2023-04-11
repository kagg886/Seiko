# Seiko

## 一.简介

Seiko 是 Mirai 的又一个 Android 端实现。有关 Mirai，请参阅 [Mirai 的 Github 仓库](https://github.com/mamoe/mirai)

它可以解决在 Android 上运行 mirai-core 程序的所有问题。通过 Seiko 可以简化在 Android 上使用 Mirai 的流程。

Seiko 还提供了一个**伪代码引擎**以供光速建立一个自动回复程序。

> 由于本人能力有限，不能兼容 mirai-console 插件。
>
> 所以这个东西还是等我更牛逼的时候再做吧x(*有生之年*)

## 二.目前支持

- 登录 (支持多 BOT)，支持滑块验证和验证码验证
- 登录成功后，可以导出信息至电脑上，格式与 MCL 通用。
- 登录失败时，会直接删掉设备信息。最大程度避免`235`风控问题
- 插件系统 (确切的说是加载器)
- 一套较为完善的伪代码引擎 (~~当然bug还是很多~~)

## 三.编写插件

1. 新建 Android 工程

2. 引入 PluginAPI 模块

3. 任取一包，在其中新建类 `B`，实现 `SeikoPlugin` 接口

4. 在 `onBotGoLine(long qq) `中实现你的代码，使用 `Bot.findInstance(long qq)` 获取新登录的 Bot。

	对bot的操作请参阅mirai文档

5. 在`resources\META-INF\services`下新建文件`com.kagg886.seiko.plugin.api.SeikoPlugin`

	将`B`的 **全限定类名**粘贴于此，保存

6. 打包成 apk 文件，挪入 `/sdcard/Android/data/com.kagg886.seiko/files/plugin`内

7. 启动 Seiko，前往插件列表查看是否有你所安装的插件

> 1. 不推荐使用 GlobalEventChannel。
> 2. 在onBotGoline(long qq)中调用Bot.close()可能会有难以置信的bug，请勿尝试

# 四.插件示例

[前往 PluginDemo 工程查看](PluginDemo)

## 五.伪代码

> 伪代码引擎已迁移到 mirai-console 并成功运行。可以前往 [ConsolePlugin](ConsolePlugin) 工程查看

[查看伪代码支持的格式和教程](DictionaryCore/README.md)

## 六.我的联系方式

[点击链接加入群聊【次元空间】](https://jq.qq.com/?_wv=1027&k=8vQZES3X)
