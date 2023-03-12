package com.kagg886.seiko.util;


import net.mamoe.mirai.utils.BotConfiguration.MiraiProtocol;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;

/**
 * @author Erzbir
 * @Date: 2023/3/11 23:29
 */
//@SuppressWarnings("all")
@SuppressWarnings({"unchecked", "rawtypes"})
public class FixProtocol {
    public static final FixProtocol INSTANCE = new FixProtocol();
    private final static Class aClass;
    private final static Constructor constructor;
    private static final Object[] ANDROID_PHONE_PROTOCOL = {
            "com.tencent.mobileqq",
            537151682,
            "8.9.33.10335",
            "6.0.0.2534",
            150470524,
            0x10400,
            16724722,
            "A6 B7 45 BF 24 A2 C2 77 52 77 16 F6 F3 6E B6 8D",
            1673599898L,
            19};
    private static final Object[] ANDROID_PAD_PROTOCOL = {
            "com.tencent.mobileqq",
            537151218,
            "8.9.33.10335",
            "6.0.0.2534",
            150470524,
            0x10400,
            16724722,
            "A6 B7 45 BF 24 A2 C2 77 52 77 16 F6 F3 6E B6 8D",
            1673599898L,
            19
    };

    private static final Object[] MACOS_PROTOCOL = {
            "com.tencent.minihd.qq",
            537128930,
            "5.8.9",
            "6.0.0.2433",
            150470524,
            66560,
            1970400,
            "AA 39 78 F4 1F D9 6F F9 91 4A 66 9E 18 64 74 C7",
            1595836208L,
            12
    };

    static {
        try {
            aClass = Class.forName("net.mamoe.mirai.internal.utils.MiraiProtocolInternal");
            constructor = aClass.getConstructor(String.class, long.class, String.class, String.class, int.class,
                    int.class, int.class, String.class, long.class, int.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private FixProtocol() {
    }

    public void fix() {
        try {
            EnumMap protocolMap = getProtocolMap();
            fixProtocol(protocolMap, MiraiProtocol.ANDROID_PHONE, ANDROID_PHONE_PROTOCOL);
            fixProtocol(protocolMap, MiraiProtocol.ANDROID_PAD, ANDROID_PAD_PROTOCOL);
            fixProtocol(protocolMap, MiraiProtocol.MACOS, MACOS_PROTOCOL);
        } catch (NoSuchFieldException | InvocationTargetException | IllegalAccessException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }

    }

    private void fixProtocol(EnumMap protocolMap, MiraiProtocol miraiProtocol, Object... params) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        protocolMap.put(miraiProtocol, createMiraiProtocolInternal(params));
    }

    private Object createMiraiProtocolInternal(Object... params) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return constructor.newInstance(params);
    }

    private EnumMap getProtocolMap() throws NoSuchFieldException, IllegalAccessException {
        Field protocols = aClass.getDeclaredField("protocols");
        protocols.setAccessible(true);
        Object o = protocols.get(EnumMap.class);
        return (EnumMap) o;
    }
}

