package com.kagg886.seiko.mock;

import com.kagg886.seiko.dic.DICList;
import com.kagg886.seiko.dic.DictionaryEnvironment;
import com.kagg886.seiko.dic.bridge.DictionaryListener;
import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.session.impl.GroupMessageRuntime;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.mock.MockBot;
import net.mamoe.mirai.mock.MockBotFactory;
import net.mamoe.mirai.mock.contact.MockGroup;
import net.mamoe.mirai.mock.contact.MockMember;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        initDictionaryEnvironment();

        MockBotFactory.initialize();
        MockBot mockBot = MockBotFactory.Companion.newMockBotBuilder().id(1693256674).create();
        MockGroup group = mockBot.addGroup(786442984,"机器人测群");
        MockMember a = group.addMember(485184047,"猫娘");
        NormalMember b = group.addMember(2513485573L,"狐狸娘");
        group.changeOwner(b);
        mockBot.login();
        mockBot.getEventChannel().subscribeAlways(GroupMessageEvent.class, event -> {
            DICList.getInstance().refresh();
            for (DictionaryFile file : DICList.getInstance()) {
                GroupMessageRuntime runtime = new GroupMessageRuntime(file,event);
                runtime.invoke(event.getMessage().contentToString());
            }
        });
        Scanner scanner = new Scanner(System.in);

        for (String s = scanner.nextLine();;s = scanner.nextLine()) {
            a.says(s);
        }
    }


    public static void initDictionaryEnvironment() throws IOException {
        DictionaryEnvironment.getInstance().setDicRoot(new File("BotMock/mock/dic"));
        DictionaryEnvironment.getInstance().setDicData(new File("BotMock/mock/dicData").toPath());
        DictionaryEnvironment.getInstance().setDicConfigPoint("BotMock/mock/dicConfig.json");
        DictionaryEnvironment.getInstance().setErrorListener((p, message) -> System.out.println(p.getName() + "---" + message));

        DICList.getInstance().refresh();
//        file = new DictionaryFile(new File(DictionaryEnvironment.getInstance().getDicRoot().getAbsolutePath() + "\\dic.txt"));
//        System.out.println(file.getFile().getAbsolutePath());
//        file.parseDICCodeFile();
    }
}