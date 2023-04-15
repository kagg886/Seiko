package com.kagg886.seiko.mock;

import com.kagg886.seiko.dic.DICList;
import com.kagg886.seiko.dic.DictionaryEnvironment;
import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.model.DICParseResult;
import com.kagg886.seiko.dic.session.impl.FriendMessageRuntime;
import com.kagg886.seiko.dic.session.impl.GroupMessageRuntime;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.mock.MockBot;
import net.mamoe.mirai.mock.MockBotFactory;
import net.mamoe.mirai.mock.contact.MockFriend;
import net.mamoe.mirai.mock.contact.MockGroup;
import net.mamoe.mirai.mock.contact.MockMember;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.mock
 * @className: MockFriendTest
 * @author: kagg886
 * @description: 好友模拟测试
 * @date: 2023/4/15 22:37
 * @version: 1.0
 */
public class MockFriendTest {

    public static void main(String[] args) throws IOException {
        MockBotFactory.initialize();
        MockBot mockBot = MockBotFactory.Companion.newMockBotBuilder().id(1693256674).create();

        MockFriend friend = mockBot.addFriend(485184047L,"绪山真寻");

        mockBot.login();
        mockBot.getEventChannel().subscribeAlways(FriendMessageEvent.class, event -> {
            DICList.getInstance().refresh();
            for (DictionaryFile file : DICList.getInstance()) {
                FriendMessageRuntime runtime = new FriendMessageRuntime(file,event);
                runtime.invoke(event.getMessage().contentToString());
            }
        });
        initDictionaryEnvironment();
        Scanner scanner = new Scanner(System.in);

        for (String s = scanner.nextLine();;s = scanner.nextLine()) {
            friend.says(s);
        }
    }


    public static void initDictionaryEnvironment() throws IOException {
        DictionaryEnvironment.getInstance().setDicRoot(new File("BotMock/mock/dic"));
        DictionaryEnvironment.getInstance().setDicData(new File("BotMock/mock/dicData").toPath());
        DictionaryEnvironment.getInstance().setDicConfigPoint("BotMock/mock/dicConfig.json");
        DictionaryEnvironment.getInstance().setErrorListener((p, message) -> System.out.println(p.getName() + "---" + message));

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
