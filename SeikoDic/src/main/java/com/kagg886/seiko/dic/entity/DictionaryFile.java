package com.kagg886.seiko.dic.entity;

import android.text.TextUtils;
import com.kagg886.seiko.dic.exception.DictionaryOnLoadException;
import com.kagg886.seiko.dic.util.ArrayIterator;
import com.kagg886.seiko.dic.util.IOUtil;
import net.mamoe.mirai.event.events.MessageEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity
 * @className: Dictionary
 * @author: kagg886
 * @description: 代表了一个伪代码实例
 * @date: 2023/1/9 19:34
 * @version: 1.0
 */
public class DictionaryFile {
    private final File dicFile;

    private final HashMap<String, ArrayList<DictionaryCode>> commands = new HashMap<>();

    public DictionaryFile(File dicFile) throws Throwable {
        //TODO 200K文件大约需要两秒的时间加载，有没有大牛愿意优化一下(
        this.dicFile = dicFile;
        String dicCodes = IOUtil.loadStringFromFile(dicFile.getAbsolutePath());
        if (dicCodes.length() == 0) {
            throw new DictionaryOnLoadException("[" + dicFile.getName() + "]为空!");
        }

        String[] lines = dicCodes.split("\n");
        int start = 0;
        for (int i = 0; i < lines.length; i++) {
            if (!TextUtils.isEmpty(lines[i])) {
                start = i;
                break;
            }
        }
        ArrayIterator<String> iterator = new ArrayIterator<>(lines);
        iterator.setLen(start);

        boolean behindLineIsEmpty = true;
        String commandRegex = null;
        ArrayList<DictionaryCode> dictionaryCodes = new ArrayList<>();

        while (iterator.hasNext()) {
            String comm = iterator.next();
            if (comm.startsWith("//")) { //注释判空处理
                comm = "";
            }
            if (behindLineIsEmpty) {
                if (TextUtils.isEmpty(comm)) {
                    System.out.println("第" + iterator.getLen() + "行为空");
                    continue;
                }
                commandRegex = comm;
                System.out.println("第" + iterator.getLen() + "行指令:" + comm);
                behindLineIsEmpty = false;
                continue;
            }
            if (TextUtils.isEmpty(comm)) {
                System.out.println("第" + iterator.getLen() + "行为空");
                if (dictionaryCodes.size() == 0) {
                    throw new DictionaryOnLoadException("指令无伪代码实现:" + commandRegex + "(" + dicFile.getName() + ":" + (iterator.getLen() - 1) + ")");
                }
                commands.put(commandRegex, dictionaryCodes);
                dictionaryCodes = new ArrayList<>();
                behindLineIsEmpty = true;
                continue;
            }
            System.out.println("第" + iterator.getLen() + "行伪代码:" + comm);
            dictionaryCodes.add(new DictionaryCode(iterator.getLen(), comm));
        }
    }

    public void invoke(MessageEvent event) {
        for (Map.Entry<String, ArrayList<DictionaryCode>> command : commands.entrySet()) {
            if (event.getMessage().contentToString().matches(command.getKey())) {
                //TODO 准备工作，然后传入方法
                for (DictionaryCode c : command.getValue()) {

                }
            }
        }
    }

    public String getName() {
        return dicFile.getName();
    }
}
