package com.kagg886.seiko.util;

import net.mamoe.mirai.utils.BotConfiguration.MiraiProtocol;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.EnumMap;

/**
 * 协议注入器
 *
 * @author Erzbir vs kagg886
 * @Date: 2023/3/11 23:29
 */

//@SuppressWarnings("all")
public class ProtocolInjector {
    private final static EnumMap<MiraiProtocol, Object> map;

    private final static EnumMap<MiraiProtocol, Object> clone;

    private final static Class<?> internalClass;
    private final static Constructor<?> constructor;

    private final static Field origin;

    static {
        try {
            internalClass = Class.forName("net.mamoe.mirai.internal.utils.MiraiProtocolInternal");

            //获取协议表
            origin = internalClass.getDeclaredField("protocols");
            origin.setAccessible(true);

            Object o = origin.get(EnumMap.class);
            map = (EnumMap<MiraiProtocol, Object>) o;

            //备份原始协议
            clone = map.clone();


            //获取协议核心构造器
            constructor = internalClass.getConstructor(
                    String.class,
                    long.class,
                    String.class,
                    String.class,
                    String.class,
                    int.class,
                    int.class,
                    int.class,
                    String.class,
                    long.class,
                    int.class,
                    String.class,
                    boolean.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    //    var buildVer: String,
    //    var appKey: String,
    private String buildVer;
    private String appKey;
    //下面是成员代码区
    private String apkId;
    private Long id;
    private String ver;
    private String sdkVer;
    private Integer miscBitMap;
    private Integer subSigMap;
    private Integer mainSigMap;
    private String sign;
    private Long buildTime;
    private Integer ssoVersion;
    private Boolean supportsQRLogin;

    public static EnumMap<MiraiProtocol, Object> getInternalProtocolMap() {
        return map;
    }

    //从0开始构造协议注入器
    public ProtocolInjector() {

    }

    //从已有的核心协议复刻一个注入器
    public ProtocolInjector(MiraiProtocol protocol) {
        Object obj = map.get(protocol); //原生协议对象

        try {
            for (Field nativeProtocolField : internalClass.getDeclaredFields()) { //原生协议属性
                if (Modifier.isStatic(nativeProtocolField.getModifiers())) continue;
                Field injecterProtocolField = getClass().getDeclaredField(nativeProtocolField.getName());

                injecterProtocolField.setAccessible(true);
                nativeProtocolField.setAccessible(true);

                injecterProtocolField.set(this, nativeProtocolField.get(obj));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //恢复协议为初始字段
    public static void restore(MiraiProtocol target) {
        map.put(target, clone.get(target));
    }

    //将字段注入到对应的协议中
    public void inject(MiraiProtocol target) {
        String sign = collectSign(this.sign);
        ObjectUtils.requireNull(apkId, id, ver, sdkVer, miscBitMap, subSigMap, mainSigMap, sign, buildTime, ssoVersion, supportsQRLogin);
        try {
            map.put(target, constructor.newInstance(apkId, id, ver, buildVer, sdkVer, miscBitMap, subSigMap, mainSigMap, sign, buildTime, ssoVersion, appKey, supportsQRLogin));
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private String collectSign(String sign) {
        //a6b745bf24a2c277527716f6f36eb68d
        //这两种均需要识别成功
        //A6 B7 45 BF 24 A2 C2 77 52 77 16 F6 F3 6E B6 8D
        if (sign.length() >= 32 && sign.length() <= 47) {
            if (!TextUtils.checkHex(sign)) {
                throw new IllegalArgumentException("Sign Not 32 byte HEX!");
            }
        } else {
            throw new IllegalArgumentException("Sign's length is error!");
        }


        if (sign.length() != 47) {
            sign = sign.replace(" ", "").toUpperCase(); //归一化到32位格式
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 32; i++) {
                if (i % 2 == 0 && i != 0) {
                    builder.append(" ");
                }
                builder.append(sign.charAt(i));
            }
            sign = builder.toString();
            //分隔字符串
        }
        return sign;
    }

    //GETTER 和 SETTER

    public String getBuildVer() {
        return buildVer;
    }

    public void setBuildVer(String buildVer) {
        this.buildVer = buildVer;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getApkId() {
        return apkId;
    }

    public void setApkId(String apkId) {
        this.apkId = apkId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getSdkVer() {
        return sdkVer;
    }

    public void setSdkVer(String sdkVer) {
        this.sdkVer = sdkVer;
    }

    public Integer getMiscBitMap() {
        return miscBitMap;
    }

    public void setMiscBitMap(Integer miscBitMap) {
        this.miscBitMap = miscBitMap;
    }

    public Integer getSubSigMap() {
        return subSigMap;
    }

    public void setSubSigMap(Integer subSigMap) {
        this.subSigMap = subSigMap;
    }

    public Integer getMainSigMap() {
        return mainSigMap;
    }

    public void setMainSigMap(Integer mainSigMap) {
        this.mainSigMap = mainSigMap;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Long getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(Long buildTime) {
        this.buildTime = buildTime;
    }

    public Integer getSsoVersion() {
        return ssoVersion;
    }

    public void setSsoVersion(Integer ssoVersion) {
        this.ssoVersion = ssoVersion;
    }

    public Boolean getSupportsQRLogin() {
        return supportsQRLogin;
    }

    public void setSupportsQRLogin(Boolean supportsQRLogin) {
        this.supportsQRLogin = supportsQRLogin;
    }
}

