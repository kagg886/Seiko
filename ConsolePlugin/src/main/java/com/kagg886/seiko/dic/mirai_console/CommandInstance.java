package com.kagg886.seiko.dic.mirai_console;

import com.kagg886.seiko.dic.DICList;
import com.kagg886.seiko.dic.entity.DictionaryCode;
import com.kagg886.seiko.dic.entity.DictionaryCommandMatcher;
import com.kagg886.seiko.dic.entity.DictionaryFile;
import net.mamoe.mirai.console.command.CommandContext;
import net.mamoe.mirai.console.command.ConsoleCommandSender;
import net.mamoe.mirai.console.command.java.JCompositeCommand;

import java.util.ArrayList;
import java.util.Map;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.mirai_console
 * @className: CommandInstance
 * @author: kagg886
 * @description: 指令集
 * @date: 2023/1/27 18:22
 * @version: 1.0
 */
public class CommandInstance extends JCompositeCommand {
    public static final CommandInstance INSTANCE = new CommandInstance();

    public CommandInstance() {
        super(PluginLoader.INSTANCE, "dic");
    }

    @SubCommand("info")
    public void info(CommandContext context, String fileName) {
        if (!(context.getSender() instanceof ConsoleCommandSender)) {
            return;
        }
        for (DictionaryFile file : DICList.getInstance()) {
            if (file.getName().equals(fileName)) {
                StringBuilder builder = new StringBuilder();
                builder.append("伪代码路径:");
                builder.append(file.getFile().getAbsolutePath());
                builder.append("\n伪代码指令:\n[生效范围]指令:行数");
                for (Map.Entry<DictionaryCommandMatcher, ArrayList<DictionaryCode>> entry : file.getCommands().entrySet()) {
                    builder.append("\n");
                    builder.append(entry.getKey().toString());
                    builder.append(":");
                    builder.append(entry.getKey().getLine());
                }
                PluginLoader.INSTANCE.getLogger().info(builder.toString());
                return;
            }
        }
        PluginLoader.INSTANCE.getLogger().info("找不到伪代码文件!");
    }

    @SubCommand("list") //dic list
    public void list(CommandContext context) {
        if (!(context.getSender() instanceof ConsoleCommandSender)) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("\n加载了");
        builder.append(DICList.getInstance().size());
        builder.append("项伪代码文件,下面是详细信息:");
        builder.append("\n文件名---指令数");
        for (DictionaryFile file : DICList.getInstance()) {
            builder.append("\n");
            builder.append(file.getName());
            builder.append("---");
            builder.append(file.getCommands().size());
        }
        builder.append("\n--------打印结束--------");
        PluginLoader.INSTANCE.getLogger().info(builder.toString());
    }

    @SubCommand("reload") //dic reload
    public void reload(CommandContext context) {
        if (!(context.getSender() instanceof ConsoleCommandSender)) {
            return;
        }
        DICList.getInstance().refresh();
        PluginLoader.INSTANCE.getLogger().info("重载所有伪代码文件...成功!");
    }
}
