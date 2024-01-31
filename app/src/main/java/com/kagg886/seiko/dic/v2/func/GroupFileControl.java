package com.kagg886.seiko.dic.v2.func;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.kagg886.seiko.dic.v2.Util;
import io.github.seikodictionaryenginev2.base.entity.code.func.Function;
import io.github.seikodictionaryenginev2.base.exception.DictionaryOnRunningException;
import io.github.seikodictionaryenginev2.base.session.BasicRuntime;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.file.AbsoluteFile;
import net.mamoe.mirai.contact.file.AbsoluteFileFolder;
import net.mamoe.mirai.contact.file.AbsoluteFolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author kagg886
 * @Date 2024/1/31 下午4:29
 * @description:
 */

public class GroupFileControl {
    private static JSONObject fileToJSON(AbsoluteFile file) { //只能是一级文件或二级文件，不可能是文件夹
        JSONObject object = new JSONObject();
        object.put("类型", "文件");

        object.put("文件名", file.getName());
        object.put("大小", file.getSize());
        object.put("下载链接", Objects.requireNonNull(file.getUrl()));

        object.put("上传者", file.getUploaderId());
        object.put("服务器路径", file.getAbsolutePath());

        object.put("上传时间", file.getUploadTime() * 1000);
        object.put("到期时间", file.getExpiryTime() * 1000); //转毫秒，符合毫秒时间戳格式

        object.put("文件id", file.getId());
        return object;
    }

    /**
     * 获取所有群文件列表
     * $删群文件 群文件对象或文件路径 群号 bot账号(可选)$ 或 $删群文件 群文件对象或文件路径 %上下文%$
     *
     * @author kagg886
     * @date 2023/5/23 12:54
     **/
    public static class Delete extends Function {
        public Delete(int line, String code) {
            super(line, code);
        }

        @Override
        protected Object run(BasicRuntime<?, ?, ?> runtime, List<Object> args) {
            Group group = Util.getGroupByObjectList(runtime, args, 1);
            Object obj = args.get(0);
            List<AbsoluteFileFolder> files = new ArrayList<>();
            if (obj instanceof Map) {
                if (!((Map<?, ?>) obj).get("类型").equals("文件")) {
                    throw new DictionaryOnRunningException("集合:" + obj + "不是文件!");
                }
                //一定是个File
                files.add(group.getFiles().getRoot().resolveFileById((String) ((Map<?, ?>) obj).get("文件id"), true));
            } else if (obj instanceof String) {
                List<AbsoluteFileFolder> folders = group.getFiles().getRoot().resolveAllStream(((String) obj)).flatMap((a) -> {
                    if (a.isFolder()) {
                        return ((AbsoluteFolder) a).childrenStream();
                    }
                    return Stream.of(a);
                }).collect(Collectors.toList());
                if (folders.size() == 0) {
                    throw new DictionaryOnRunningException("此Path下文件为空:" + obj);
                }
                files.addAll(folders);
            } else {
                throw new DictionaryOnRunningException("非法的第一个参数!");
            }

            files.forEach(AbsoluteFileFolder::delete);
        }


    }

    /**
     * 获取所有群文件列表
     * $群文件 群号 bot账号(可选)$ 或 $群文件 %上下文%$
     *
     * @author kagg886
     * @date 2023/5/19 17:24
     **/
    public static class GetFile extends Function {
        public GetFile(int line, String code) {
            super(line, code);
        }

        @Override
        protected Object run(BasicRuntime<?, ?, ?> runtime, List<Object> args) {
            Group group = Util.getGroupByObjectList(runtime, args, 0);

            return group.getFiles().getRoot().childrenStream()
                    .map((v) -> {
                        if (v instanceof AbsoluteFile) { //文件
                            return fileToJSON((AbsoluteFile) v);
                        }
                        if (v instanceof AbsoluteFolder) { //目录
                            return new JSONArray(((AbsoluteFolder) v)
                                    .childrenStream()
                                    .map((tri_folder) -> ((AbsoluteFile) tri_folder))
                                    .map(GroupFileControl::fileToJSON)
                                    .collect(Collectors.toList()));
                        }
                        throw new RuntimeException();
                    })
                    .collect(Collectors.toList());
        }
    }
}
