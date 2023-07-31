package com.kagg886.seiko.dic.entity.func.impl;

import com.kagg886.seiko.dic.DictionaryEnvironment;
import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.AbsRuntime;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
 * @className: MessageControl
 * @author: kagg886
 * @description: 消息有关
 * @date: 2023/3/19 22:15
 * @version: 1.0
 */
public abstract class MessageControl extends Function.UnInterruptedFunction {

    /**
     * $戳 %成员对象%$ 或 $戳 %上下文%$
     *
     * @author kagg886
     * @date 2023/7/31 11:03
     **/
    public static class Nudge extends Function.InterruptedFunction {
        public Nudge(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            Object arg0 = args.get(0);
            if (arg0 instanceof HashMap) {
                HashMap<String, ?> memberOrFriend = ((HashMap<String, ?>) arg0);
                String type = (String) memberOrFriend.get("类型");
                Bot b;
                switch (type) {
                    case "群成员":
                        b = Bot.findInstance((long) memberOrFriend.get("BOT"));
                        Group g = b.getGroup((long) memberOrFriend.get("群号"));
                        Member m = g.get((long) memberOrFriend.get("QQ"));
                        m.nudge().sendTo(g);
                        break;
                    case "好友":
                        b = Bot.findInstance((long) memberOrFriend.get("BOT"));
                        Friend f = b.getFriend((long) memberOrFriend.get("QQ"));
                        f.nudge().sendTo(f);
                        break;
                    default:
                        throw new DictionaryOnRunningException("非法的戳一戳类型" + type);
                }
            }
            if (arg0 instanceof GroupMessageEvent) {
                ((GroupMessageEvent) arg0).getSender().nudge().sendTo(((GroupMessageEvent) arg0).getGroup());
                return;
            }

            if (arg0 instanceof FriendMessageEvent) {
                ((FriendMessageEvent) arg0).getFriend().nudge().sendTo(((FriendMessageEvent) arg0).getFriend());
                return;
            }
            throw new DictionaryOnRunningException("不合法的调用");
        }
    }


    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: addAt
     * @author: kagg886
     * @description: $设置回复 %上下文% 待回复文字$
     * @date: 2023/4/9 19:17
     * @version: 1.0
     */
    public static class Reply extends Function.InterruptedFunction implements ArgumentLimiter {

        public Reply(int line, String code) {
            super(line, code);
        }

        @Override
        public void run(AbsRuntime<?> runtime, List<Object> args) {
            MessageEvent event = (MessageEvent) args.get(0);

            MessageSource source1;
            if (args.size() == 2) {
                OnlineMessageSource source = event.getSource();
                MessageSourceKind kind;
                if (source instanceof OnlineMessageSource.Incoming.FromGroup) {
                    kind = MessageSourceKind.GROUP;
                } else if (source instanceof OnlineMessageSource.Incoming.FromFriend) {
                    kind = MessageSourceKind.FRIEND;
                } else {
                    throw new DictionaryOnRunningException("不支持的类型:" + source.getClass().getName());
                }

                MessageSourceBuilder clone = new MessageSourceBuilder();
                clone.setIds(source.getIds());
                clone.setTime(source.getTime());
                clone.setFromId(source.getFromId());
                clone.setTargetId(source.getTargetId());
                clone.setInternalIds(source.getInternalIds());
                clone.messages(new PlainText(args.get(1).toString()));
                source1 = clone.build(event.getBot().getId(), kind);
            } else {
                source1 = event.getSource();
            }
            QuoteReply quoteReply = new QuoteReply(source1);
            runtime.getMessageCache().append(quoteReply);
        }

        @Override
        public int getArgumentLength() {
            return 2;
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: addAt
     * @author: kagg886
     * @description: $撤回 %上下文%$
     * @date: 2023/4/9 19:11
     * @version: 1.0
     */
    public static class Recall extends Function.UnInterruptedFunction {

        public Recall(int line, String code) {
            super(line, code);
        }

        @Override
        public void run(AbsRuntime<?> runtime, List<Object> args) {
            MessageEvent event = (MessageEvent) args.get(0);
            MessageSource.recall(event.getMessage());
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: addAt
     * @author: kagg886
     * @description: $设置接收者 %群对象/成员对象/好友对象%$
     * @date: 2023/4/1 10:42
     * @version: 1.0
     */
    public static class setSender extends Function.InterruptedFunction {

        public setSender(int line, String code) {
            super(line, code);
        }

        @Override
        public void run(AbsRuntime<?> runtime, List<Object> args) {
            if (args.get(0) instanceof Map<?, ?>) {
                Map<?, ?> map = (Map<?, ?>) args.get(0);
                Object type = map.getOrDefault("类型", null);
                Contact contact;

                Bot bot = Bot.findInstance(Long.parseLong(map.get("BOT").toString()));
                if (type.equals("群")) {
                    contact = bot.getGroup(Long.parseLong(map.get("群号").toString()));
                } else if (type.equals("好友")) {
                    contact = bot.getFriend(Long.parseLong(map.get("QQ").toString()));
                } else if (type.equals("群成员")) {
                    contact = bot.getGroup(Long.parseLong(map.get("群号").toString()))
                            .getMembers().get(Long.parseLong(map.get("QQ").toString()));
                } else {
                    throw new DictionaryOnRunningException("未知的联系人类型:" + map.get("类型"));
                }
                runtime.setContact(contact);
            } else {
                throw new DictionaryOnRunningException("传参不是集合对象!");
            }

        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: addAt
     * @author: kagg886
     * @description: $艾特 qq$(当QQ为0时为全体艾特)
     * @date: 2023/1/18 12:25
     * @version: 1.0
     */
    public static class addAt extends Function.UnInterruptedFunction {

        public addAt(int line, String code) {
            super(line, code);
        }

        @Override
        public void run(AbsRuntime<?> runtime, List<Object> args) {
            long at = Long.parseLong(args.get(0).toString());
            if (at != 0) {
                runtime.getMessageCache().append(new At(at));
            } else {
                runtime.getMessageCache().append(AtAll.INSTANCE);
            }
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: addImage
     * @author: kagg886
     * @description: $图片 link$
     * @date: 2023/1/18 12:25
     * @version: 1.0
     */
    public static class addImage extends Function.UnInterruptedFunction {

        public addImage(int line, String code) {
            super(line, code);
        }

        @Override
        public void run(AbsRuntime<?> runtime, List<Object> args) {
            String url = args.get(0).toString();
            try (InputStream s = Jsoup.connect(url).ignoreContentType(true).execute().bodyStream()) {
                runtime.getMessageCache().append(ExternalResource.uploadAsImage(s, runtime.getContact()));
            } catch (Exception ignored) {
                try (InputStream s = Files.newInputStream(new File(DictionaryEnvironment.getInstance().getDicData().toFile().getAbsolutePath() + "/" + url).toPath())) {
                    runtime.getMessageCache().append(ExternalResource.uploadAsImage(s, runtime.getContact()));
                } catch (IOException e) {
                    throw new DictionaryOnRunningException("上传图片失败!:" + args.get(0) + "在" + runtime.getFile().getFile().getAbsolutePath() + ":" + getLine(), e);
                }
            }
//            Connection conn = Jsoup.connect(args.get(0).toString()).ignoreContentType(true);
//            try (InputStream s = conn.execute().bodyStream()) {
//                runtime.getMessageCache().append(ExternalResource.uploadAsImage(s, runtime.getContact()));
//            } catch (IOException e) {
//                throw new DictionaryOnRunningException("上传图片失败!:" + args.get(0) + "在" + runtime.getFile().getFile().getAbsolutePath() + ":" + getLine(), e);
//            }
        }
    }


    public MessageControl(int line, String code) {
        super(line, code);
    }
}
