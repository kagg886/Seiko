package com.kagg886.seiko.dic.mirai_console

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.mirai_console
 * @className: SeikoPluginConfig
 * @author: kagg886
 * @description: 插件的配置文件存储处
 * @date: 2023/1/27 18:22
 * @version: 1.0
 */
object SeikoPluginConfig : AutoSavePluginConfig("pluginConfig") {
    val alwaysRefreshOnceMessageGetting by value<Boolean>(false) //每次接受到消息时都要刷新一遍伪代码
}