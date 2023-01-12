package com.kagg886.seiko.plugin;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.plugin.api
 * @className: SeikoDescription
 * @author: kagg886
 * @description: 插件描述类，加载时调用
 * @date: 2022/12/22 15:02
 * @version: 1.0
 */
public class SeikoDescription {
    private String name;
    private String desc;
    private String author;
    private String verCode;
    private final String id;

    public SeikoDescription(String id) {
        this.id = id;
    }

    public String getVerCode() {
        return verCode;
    }

    public String getAuthor() {
        return author;
    }

    public String getDesc() {
        return desc;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setVerCode(String verCode) {
        this.verCode = verCode;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
