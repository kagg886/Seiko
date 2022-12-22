# Seiko

## 简介
Seiko是一个基于Mirai，并适用于Android端的一个QQ机器人软件<br>
有关Mirai，请参阅[Mirai的Github仓库](https://github.com/mamoe/mirai)

## 目前支持
- 登录，支持滑块验证和验证码验证
- 导出登录设备信息
- 简单的插件系统

## 编写插件
1. 下载PluginModule文件夹
2. 将src/java内文件挪到你的工程中
3. 新建包，在包内新建一个类A，实现SeikoPlugin接口
4. 在onBotGoLine(long qq)中实现你的代码，使用<br>
``` Bot.findInstance(long qq) ```<br>
获取新登录的Bot。不推荐使用GlobalEventChannel。
5. 编译，使用d8工具转换成dex，重命名dex为"A的全限定类名.dex"，然后扔进<br>
`sdcard/Android/data/com.kagg886.seiko/files/plugin`
<br>内
6. 启动Seiko，前往插件列表查看是否有你所安装的插件