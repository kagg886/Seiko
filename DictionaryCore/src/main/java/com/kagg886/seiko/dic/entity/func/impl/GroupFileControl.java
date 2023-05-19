package com.kagg886.seiko.dic.entity.func.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.session.AbsRuntime;
import com.kagg886.seiko.dic.util.DictionaryUtil;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.file.AbsoluteFile;
import net.mamoe.mirai.contact.file.AbsoluteFolder;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 群文件管理函数
 *
 * @author kagg886
 * @date 2023/5/19 17:23
 **/
public abstract class GroupFileControl extends Function.UnInterruptedFunction {

    public GroupFileControl(int line, String code) {
        super(line, code);
    }

    /**
     * 获取所有群文件列表
     * $群文件 存入变量 对象$
     *
     * @author kagg886
     * @date 2023/5/19 17:24
     **/
    public static class GetFile extends GroupFileControl {
        public GetFile(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            Group group = DictionaryUtil.getGroupByObjectList(runtime, args, 1);

            List<JSON> folders = group.getFiles().getRoot().childrenStream()
                    .map((v) -> {
                        if (v instanceof AbsoluteFile) { //文件
                            return fileToJSON((AbsoluteFile) v);
                        }
                        if (v instanceof AbsoluteFolder) { //目录
                            return new JSONArray(((AbsoluteFolder) v)
                                    .childrenStream()
                                    .map((tri_folder) -> ((AbsoluteFile) tri_folder))
                                    .map(this::fileToJSON)
                                    .collect(Collectors.toList()));
                        }
                        throw new RuntimeException();
                    })
                    .collect(Collectors.toList());
            runtime.getRuntimeObject().put(args.get(0).toString(),folders);
        }


        private JSONObject fileToJSON(AbsoluteFile file) { //只能是一级文件或二级文件，不可能是文件夹
            JSONObject object = new JSONObject();
            object.put("类型", "文件");

            object.put("文件名", file.getName());
            object.put("大小", file.getSize());
            object.put("下载链接", Objects.requireNonNull(file.getUrl()));

            object.put("上传者", file.getUploaderId());
            object.put("服务器路径", file.getAbsolutePath());

            object.put("上传时间", file.getUploadTime() * 1000);
            object.put("到期时间", file.getExpiryTime() * 1000); //转毫秒，符合毫秒时间戳格式
            return object;
        }
    }
}
