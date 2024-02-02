package com.kagg886.seiko.util;

public class AntiDetect {
    static {
        System.loadLibrary("seiko");
    }

    public static native void init();
}
