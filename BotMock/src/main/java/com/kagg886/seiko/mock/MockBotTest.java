package com.kagg886.seiko.mock;

import com.kagg886.seiko.dic.DICList;
import com.kagg886.seiko.dic.DictionaryEnvironment;
import com.kagg886.seiko.dic.DictionaryReg;
import com.kagg886.seiko.dic.model.DICParseResult;
import net.mamoe.mirai.contact.file.AbsoluteFolder;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.mock.MockBot;
import net.mamoe.mirai.mock.MockBotFactory;
import net.mamoe.mirai.mock.contact.MockGroup;
import net.mamoe.mirai.mock.contact.MockMember;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

public class MockBotTest {
    public static void main(String[] args) throws IOException {
        initDictionaryEnvironment();
//        List<Integer> a = List.of(1,2,3,4,5);
//        System.out.println(JSON.toJSONString(a));
        MockBotFactory.initialize();

        //初始化bot
        MockBot mockBot = MockBotFactory.Companion.newMockBotBuilder().id(1693256674).create();

        //初始化群
        MockGroup group = mockBot.addGroup(1, "机器人测群");
        MockGroup group1 = mockBot.addGroup(2, "草");

        //初始化自己
        MockMember a = group.addMember(485184047, "猫娘");

        for (int i = 100000; i < 100100; i++) {
            group.addMember(i, UUID.randomUUID().toString());
        }
        //将自己设为群主
        group.changeOwner(group.getBotAsMember());

        //上传群文件
        group.getFiles().uploadNewFile("a.txt", ExternalResource.create(new File("build.gradle")));
        group.getFiles().uploadNewFile("b.txt", ExternalResource.create(new File("build.gradle")));
        group.getFiles().uploadNewFile("c.txt", ExternalResource.create(new File("build.gradle")));

        AbsoluteFolder folder = group.getFiles().getRoot().createFolder("list1");
        folder.uploadNewFile("a.txt",ExternalResource.create(new File("build.gradle")));
        folder.uploadNewFile("b.txt",ExternalResource.create(new File("build.gradle")));

        folder = group.getFiles().getRoot().createFolder("list2");
        folder.uploadNewFile("a.txt",ExternalResource.create(new File("build.gradle")));
        folder.uploadNewFile("b.txt",ExternalResource.create(new File("build.gradle")));

        mockBot.login();


        DictionaryReg.reg(mockBot.getEventChannel(),true,((logger, result) -> {
            result.err.forEach(logger::error);
        }));

        initDictionaryEnvironment();
        Scanner scanner = new Scanner(System.in);

        for (String s = scanner.nextLine();;s = scanner.nextLine()) {
            //group.broadcastNewMemberJoinRequestEvent(123456,"猫娘",s,0);
            a.says(new MessageChainBuilder().append(new PlainText(s))
                    .append(ExternalResource.uploadAsImage(new File("BotMock/mock/dic/dic.txt"), group)).build());
        }
    }


    public static void initDictionaryEnvironment() throws IOException {
        DictionaryEnvironment.getInstance().setDicRoot(new File("BotMock/mock/dic"));
        DictionaryEnvironment.getInstance().setDicData(new File("BotMock/mock/dicData").toPath());
        DictionaryEnvironment.getInstance().setDicConfigPoint("BotMock/mock/dicConfig.json");
        DictionaryEnvironment.getInstance().setErrorListener((p, message) -> System.out.println(p.getName() + "---" + message));

        //如果使用mock会打印两次日志，属于正常现象
        DICParseResult result = DICList.getInstance().refresh();
        if (!result.success) {
            for (Throwable e : result.err) {
                e.printStackTrace();
            }
        }
//        file = new DictionaryFile(new File(DictionaryEnvironment.getInstance().getDicRoot().getAbsolutePath() + "\\dic.txt"));
//        System.out.println(file.getFile().getAbsolutePath());
//        file.parseDICCodeFile();
    }
}