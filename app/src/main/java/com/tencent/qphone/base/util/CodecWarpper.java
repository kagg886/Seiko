package com.tencent.qphone.base.util;

import android.content.Context;
import com.tencent.mobileqq.msf.MsfService;
import com.tencent.qphone.base.remote.FromServiceMsg;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

import moe.fuqiuluo.signfaker.logger.TextLogger;
public abstract class CodecWarpper {
    public static final int CODE_FAIL = -1;
    public static final int CODE_FAIL_DECRYPT_DATA_LEN_ERROR = -5;
    public static final int CODE_FAIL_DECRYPT_EMPTY = -4;
    public static final int CODE_FAIL_DECRYPT_ONCE = -2;
    public static final int CODE_FAIL_DECRYPT_TWICE = -3;
    public static final int CODE_FAIL_PBUNPACK = -8;
    public static final int CODE_FAIL_ZLIB_DATA_LEN_SHORT = -6;
    public static final int CODE_FAIL_ZLIB_UNCOMPRESS_ERROR = -7;
    public static final int CODE_SUCC_DECRYPT_ONCE = 1;
    public static final int CODE_SUCC_DECRYPT_TWICE = 2;
    private static int checkedSOVersion = 0;
    public static boolean isLoaded = false;
    public static int soLoadResultCode = 0;
    public static String tag = "MSF.C.CodecWarpper";
    protected boolean mIsMain = true;
    private static final Object mLockObj = new Object();
    public static final AtomicBoolean isInitNoLoginWhiteList = new AtomicBoolean(false);

    static {
        System.loadLibrary("codecwrapperV2");
        checkSOAppId();
        checkSOVersion();
    }

    public static void checkSOAppId() {
        try {
            int appId = 537168313;
            boolean h2 = true;
            initAppId(appId, h2);
        } catch (UnsatisfiedLinkError e2) {
        } catch (Throwable th) {
        }
    }

    public static void checkSOVersion() {
        try {
            checkedSOVersion = getSOVersion();
            String str = tag;
            TextLogger.INSTANCE.log("checkSO version=" + checkedSOVersion);
        } catch (UnsatisfiedLinkError e2) {
        } catch (Throwable th) {
        }
    }

    private static native void closeReceData();

    private static native synchronized byte[] encodeRequest(int i2, String str, String str2, String str3, String str4, String str5, byte[] bArr, int i3, int i4, String str6, byte b2, byte b3, byte b4, byte[] bArr2, byte[] bArr3, byte[] bArr4, boolean z);

    private static native synchronized byte[] encodeRequest(int i2, String str, String str2, String str3, String str4, String str5, byte[] bArr, int i3, int i4, String str6, byte b2, byte b3, byte[] bArr2, boolean z);

    private static native synchronized byte[] encodeRequest(int i2, String str, String str2, String str3, String str4, String str5, byte[] bArr, int i3, int i4, String str6, byte b2, byte b3, byte[] bArr2, byte[] bArr3, byte[] bArr4, boolean z);

    public static native int getAppid();

    public static native int getCurrentSSOVersion(boolean z);

    public static native byte[] getFileStoreKey();

    public static native int getMaxPackageSize();

    public static native long getPacketLossLength(int i2);

    public static native int getSOVersion();

    public static int getSharedObjectVersion() {
        return checkedSOVersion;
    }

    public static native long getSocketTotalRetrans(int i2);

    public static native int getVersionCode();

    private static native synchronized void initAppId(int i2, boolean z);

    public static byte[] nativeEncodeRequest(int i2, String str, String str2, String str3, String str4, String str5, byte[] bArr, int i3, int i4, String str6, byte b2, byte b3, byte[] bArr2, byte[] bArr3, byte[] bArr4, boolean z) {
        try {
            return encodeRequest(i2, str, str2, str3, str4, str5, bArr, i3, i4, str6, b2, b3, bArr2, bArr3, bArr4, z);
        } catch (Exception e2) {
            TextLogger.INSTANCE.log("nativeEncodeRequest 2 exception: " + e2);
            return null;
        }
    }

    public static int nativeGetCurrentSSOVersionCode(boolean z) {
        try {
            return getCurrentSSOVersion(z);
        } catch (UnsatisfiedLinkError e2) {
            TextLogger.INSTANCE.log("nativeEncodeRequest 2 exception: " + e2);
            return 0;
        }
    }

    public static byte[] nativeGetFileStoreKey() {
        try {
            return getFileStoreKey();
        } catch (UnsatisfiedLinkError e2) {
            TextLogger.INSTANCE.log("nativeEncodeRequest 2 exception: " + e2);
            return null;
        }
    }

    public static void nativeInitNoLoginWhiteList(HashSet<String> hashSet) {
        if (isInitNoLoginWhiteList.compareAndSet(false, true)) {
            try {
                setNoLoginCmd((String[]) new ArrayList(hashSet).toArray(new String[0]));
            } catch (UnsatisfiedLinkError e2) {
                TextLogger.INSTANCE.log("nativeEncodeRequest 2 exception: " + e2);
            }
        }
    }

    public static void nativeOnConnClose(boolean z) {
        onConnClose(z);
    }

    public static void nativeRemoveAccountKey(String str) {
        removeAccountKey(str);
    }

    public static void nativeSetAccountKey(String str, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5, byte[] bArr6, byte[] bArr7, byte[] bArr8, String str2) {
        StringBuilder sb = new StringBuilder("nativeSetAccountKey ");
        sb.append(",uin=");
        sb.append(str);
        printBytes(",A1=", bArr, sb);
        printBytes(",A2=", bArr2, sb);
        printBytes(",A3=", bArr3, sb);
        printBytes(",D1=", bArr4, sb);
        printBytes(",D2=", bArr5, sb);
        printBytes(",S2=", bArr6, sb);
        printBytes(",key=", bArr7, sb);
        printBytes(",cookie=", bArr8, sb);
        TextLogger.INSTANCE.log("nativeEncodeRequest 2 exception: " + sb.toString());
        setAccountKey(str, bArr, bArr2, bArr3, bArr4, bArr5, bArr6, bArr7, bArr8, str2);
    }

    public static void nativeSetIsSupportNewSSO(boolean z, boolean z2) {
        try {
            setIsSupportNewSSO(z, z2);
            String str = tag;
            TextLogger.INSTANCE.log("[nativeSetIsSupportNewSSOVersion], isSupportNewSSOVersion: " + z);
        } catch (Throwable th) {
            TextLogger.INSTANCE.log("setIsSupportNewSSO error");
        }
    }

    public static void nativeSetKsid(byte[] bArr) {
        try {
            setKsid(bArr);
        } catch (UnsatisfiedLinkError unused) {
            TextLogger.INSTANCE.log("setKsid failed");
        }
    }

    public static void nativeSetUseSimpleHead(String str, boolean z, boolean z2) {
        setUseSimpleHead(str, z, z2);
    }

    private static native synchronized void onConnClose(boolean z);

    private native void onReceData(byte[] bArr, int i2, boolean z);

    private native FromServiceMsg parseData(byte[] bArr);

    public static void printBytes(String str, byte[] bArr, StringBuilder sb) {
        sb.append(str);
        if (bArr != null && bArr.length > 0) {
            sb.append(bArr.length);
            sb.append("|");
            sb.append(buf_to_string(bArr, 1));
            return;
        }
        sb.append("null");
    }

    public static String buf_to_string(byte[] bArr) {
        String str = "";
        if (bArr == null) {
            return "";
        }
        for (int i2 = 0; i2 < bArr.length; i2++) {
            str = (str + Integer.toHexString((bArr[i2] >> 4) & 15)) + Integer.toHexString(bArr[i2] & 15);
        }
        return str;
    }

    public static String buf_to_string(byte[] bArr, int i2) {
        String str = "";
        if (bArr == null) {
            return "";
        }
        if (i2 > bArr.length) {
            i2 = bArr.length;
        }
        for (int i3 = 0; i3 < i2; i3++) {
            str = (str + Integer.toHexString((bArr[i3] >> 4) & 15)) + Integer.toHexString(bArr[i3] & 15);
        }
        return str;
    }

    private static native synchronized void removeAccountKey(String str);

    private static native synchronized void setAccountKey(String str, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5, byte[] bArr6, byte[] bArr7, byte[] bArr8, String str2);

    public static native synchronized void setIsSupportNewSSO(boolean z, boolean z2);

    private static native synchronized void setKsid(byte[] bArr);

    public static native void setMaxPackageSize(int i2);

    private static native void setNoLoginCmd(String[] strArr);

    private static native synchronized void setUseSimpleHead(String str, boolean z, boolean z2);
    public native void init(Context context, boolean z, boolean z2);

    public void nativeClearReceData() {
        TextLogger.INSTANCE.log("nativeClearReceData");
        synchronized (mLockObj) {
            try {
                closeReceData();
            } catch (UnsatisfiedLinkError e2) {
                TextLogger.INSTANCE.log("clear rece failed: " + e2);
            }
        }
    }

    public void nativeOnReceData(byte[] bArr, int i2) {
        synchronized (mLockObj) {
            onReceData(bArr, i2, this.mIsMain);
        }
    }

    public FromServiceMsg nativeParseData(byte[] bArr) {
        return parseData(bArr);
    }

    public abstract void onInvalidData(int i2, int i3, String str);

    public void onInvalidDataNative(int i2) {
        onInvalidData(-5, i2, MsfService.INSTANCE.getMainAccount());
    }

    public abstract void onInvalidSign();

    public abstract void onResponse(int i2, Object obj, int i3);

    public abstract void onResponse(int i2, Object obj, int i3, byte[] bArr);

    public abstract int onSSOPingResponse(byte[] bArr, int i2);

    public static byte[] nativeEncodeRequest(int i2, String str, String str2, String str3, String str4, String str5, byte[] bArr, int i3, int i4, String str6, byte b2, byte b3, byte b4, byte[] bArr2, byte[] bArr3, byte[] bArr4, boolean z) {
        try {
            return encodeRequest(i2, str, str2, str3, str4, str5, bArr, i3, i4, str6, b2, b3, b4, bArr2, bArr3, bArr4, z);
        } catch (Exception e2) {
            TextLogger.INSTANCE.log("nativeEncodeRequest 1 exception");
            return null;
        }
    }

    public static byte[] nativeEncodeRequest(int i2, String str, String str2, String str3, String str4, String str5, byte[] bArr, int i3, int i4, String str6, byte b2, byte b3, byte[] bArr2, boolean z) {
        try {
            return encodeRequest(i2, str, str2, str3, str4, str5, bArr, i3, i4, str6, b2, b3, bArr2, z);
        } catch (Exception e2) {
            TextLogger.INSTANCE.log("nativeEncodeRequest 3 exception");
            return null;
        }
    }
}
