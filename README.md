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
1. 下载PluginModule文件夹
2. 将src/main/java内文件挪到你的工程中
3. 新建包，在包内新建一个类A，实现SeikoPlugin接口
4. 在onBotGoLine(long qq)中实现你的代码，使用`Bot.findInstance(long qq)`获取新登录的Bot。 
5. 编译，使用d8工具转换成dex，重命名dex为"A的全限定类名.dex"，然后扔进`sdcard/Android/data/com.kagg886.seiko/files/plugin`内
6. 启动Seiko，前往插件列表查看是否有你所安装的插件
> 不推荐使用GlobalEventChannel。
## 四.插件示例
```java
package com.kagg886.seikoplugin.demo;

import android.content.Context;
import com.kagg886.seiko.plugin.SeikoDescription;
import com.kagg886.seiko.plugin.api.SeikoPlugin;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.events.BotEvent;
import net.mamoe.mirai.event.events.GroupEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.function.Consumer;

/**
 * @projectName: DemoPlugin
 * @package: com.kagg886.seikoplugin.demo
 * @className: DemoPlugin
 * @author: kagg886
 * @description: 测试插件
 * @date: 2023/1/10 13:17
 * @version: 1.0
 */
public class DemoPlugin implements SeikoPlugin {
    private Context context;

    /*
     * @param long:
     * @return null
     * @author kagg886
     * @description bot上线时调用
     * @date 2023/01/10 13:19
     */
    @Override
    public void onBotGoLine(long botQQ) {
        EventChannel<BotEvent> event = Bot.findInstance(botQQ).getEventChannel();
        event.subscribeAlways(GroupMessageEvent.class,new Consumer<GroupMessageEvent>() {
            @Override
            public void accept(GroupMessageEvent groupMessageEvent) {
                //TODO 在此处构建消息包并发送到词库引擎
            }
        });
    }

    /*
     * @param long:
     * @return null
     * @author kagg886
     * @description bot下线时调用(准确来说是BOT线程被终止时调用)(即将弃用此特性)
     * @date 2023/01/10 13:18
     */
    @Override
    public void onBotOffLine(long botQQ) {

    }
    /*
     * @param Context: 
      * @return null
     * @author kagg886
     * @description 插件被Seiko加载时调用
     * @date 2023/01/10 13:18
     */
    @Override
    public void onLoad(Context context) {
        this.context = context;
    }

    /*
     * @param null: 
      * @return null
     * @author kagg886
     * @description 插件在此处上传自己的描述
     * @date 2023/01/10 13:19
     */
    @Override
    public SeikoDescription getDescription() {
        SeikoDescription description = new SeikoDescription(getClass().getName());
        description.setName("DemoPlugin");
        description.setDesc("SeikoDemo");
        description.setAuthor("kagg886");
        description.setVerCode("0.0.1");
        return description;
    }
}
```
## 五.词库示例
等待施工
## 六.我的联系方式
[点击链接加入群聊【次元空间】](https://jq.qq.com/?_wv=1027&k=8vQZES3X)
