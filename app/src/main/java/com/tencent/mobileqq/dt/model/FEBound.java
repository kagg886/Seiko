package com.tencent.mobileqq.dt.model;

import moe.fuqiuluo.signfaker.logger.TextLogger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

public class FEBound {
    private static final int LEVEL1 = 32;
    private static final int LEVEL2 = 16;
    private static final String TAG = "FEBound";
    private static final int Type_Decode = 2;
    private static final int Type_Encode = 1;
    private static AtomicBoolean mInit = new AtomicBoolean(false);
    private static byte[][] mConfigEnCode = {
            new byte[]{14, 1, 13, 10, 2, 7, 6, 5, 0, 12, 4, 8, 11, 9, 15, 3},
            new byte[]{10, 0, 12, 6, 9, 4, 8, 15, 3, 7, 13, 5, 14, 11, 1, 2},
            new byte[]{6, 2, 0, 13, 4, 10, 14, 11, 15, 1, 12, 3, 9, 5, 8, 7},
            new byte[]{2, 10, 4, 8, 15, 14, 0, 3, 13, 12, 11, 6, 7, 5, 1, 9},
            new byte[]{14, 3, 11, 12, 0, 6, 9, 13, 8, 4, 7, 1, 2, 15, 10, 5},
            new byte[]{10, 11, 5, 4, 8, 12, 13, 0, 15, 7, 1, 9, 2, 6, 3, 14},
            new byte[]{7, 1, 9, 14, 3, 5, 11, 13, 6, 2, 12, 10, 0, 15, 4, 8},
            new byte[]{3, 10, 0, 5, 6, 7, 11, 2, 4, 14, 15, 1, 12, 8, 13, 9},
            new byte[]{15, 2, 14, 13, 1, 0, 4, 7, 5, 3, 9, 10, 8, 6, 11, 12},
            new byte[]{11, 10, 8, 4, 6, 0, 12, 5, 7, 9, 13, 2, 1, 3, 14, 15},
            new byte[]{7, 3, 1, 12, 14, 8, 10, 5, 6, 13, 11, 15, 4, 9, 2, 0},
            new byte[]{3, 11, 13, 0, 6, 5, 7, 8, 9, 1, 15, 14, 12, 10, 2, 4},
            new byte[]{15, 3, 7, 11, 14, 5, 12, 2, 13, 4, 10, 0, 1, 8, 9, 6},
            new byte[]{11, 12, 2, 3, 5, 14, 1, 15, 7, 10, 4, 8, 9, 6, 13, 0},
            new byte[]{7, 4, 12, 10, 13, 11, 8, 2, 14, 15, 0, 6, 3, 1, 5, 9},
            new byte[]{3, 12, 7, 4, 2, 8, 13, 9, 6, 11, 10, 14, 0, 15, 1, 5},
            new byte[]{0, 3, 10, 11, 1, 12, 9, 4, 7, 6, 15, 5, 13, 2, 14, 8},
            new byte[]{12, 11, 5, 3, 13, 14, 15, 6, 4, 8, 9, 10, 2, 7, 1, 0},
            new byte[]{8, 3, 15, 10, 11, 4, 9, 13, 1, 7, 6, 14, 5, 0, 12, 2},
            new byte[]{4, 12, 10, 6, 13, 9, 5, 7, 2, 8, 11, 0, 3, 14, 15, 1},
            new byte[]{0, 4, 1, 10, 11, 5, 6, 15, 13, 9, 3, 2, 8, 14, 12, 7},
            new byte[]{12, 0, 14, 15, 9, 1, 3, 13, 2, 4, 7, 6, 10, 8, 11, 5},
            new byte[]{8, 5, 9, 2, 10, 14, 11, 13, 7, 0, 1, 12, 6, 3, 15, 4},
            new byte[]{4, 13, 3, 9, 5, 1, 11, 2, 6, 8, 10, 0, 15, 14, 7, 12},
            new byte[]{0, 5, 14, 8, 10, 3, 2, 7, 13, 1, 11, 12, 9, 15, 6, 4},
            new byte[]{13, 12, 14, 15, 7, 1, 9, 2, 11, 5, 8, 10, 3, 0, 4, 6},
            new byte[]{9, 4, 12, 0, 8, 14, 1, 3, 11, 6, 5, 13, 10, 2, 15, 7},
            new byte[]{5, 12, 6, 15, 10, 13, 14, 3, 9, 8, 2, 11, 0, 1, 7, 4},
            new byte[]{1, 5, 7, 2, 6, 11, 4, 13, 3, 10, 0, 9, 15, 14, 12, 8},
            new byte[]{13, 0, 11, 15, 8, 4, 9, 3, 1, 5, 14, 10, 7, 2, 6, 12},
            new byte[]{9, 5, 6, 7, 3, 15, 14, 4, 2, 8, 13, 0, 1, 11, 10, 12},
            new byte[]{5, 14, 1, 2, 13, 0, 7, 12, 9, 11, 8, 4, 3, 10, 6, 15},
    };
    private static byte[][] mConfigDeCode = {
            new byte[]{11, 15, 6, 12, 5, 4, 1, 8, 13, 7, 3, 14, 0, 9, 2, 10},
            new byte[]{14, 4, 10, 7, 15, 13, 8, 5, 0, 2, 12, 6, 3, 11, 1, 9},
            new byte[]{9, 10, 15, 2, 0, 4, 1, 8, 13, 3, 7, 11, 5, 12, 6, 14},
            new byte[]{6, 0, 12, 3, 7, 5, 15, 2, 13, 10, 11, 4, 8, 1, 14, 9},
            new byte[]{4, 8, 13, 6, 5, 1, 3, 12, 11, 2, 7, 15, 14, 0, 10, 9},
            new byte[]{3, 7, 15, 11, 5, 9, 13, 12, 6, 0, 14, 10, 2, 1, 4, 8},
            new byte[]{7, 2, 8, 6, 11, 0, 1, 14, 15, 3, 9, 4, 13, 5, 12, 10},
            new byte[]{10, 0, 12, 5, 9, 13, 3, 14, 2, 4, 15, 6, 7, 8, 1, 11},
            new byte[]{4, 2, 5, 14, 1, 13, 7, 11, 10, 12, 6, 3, 9, 8, 0, 15},
            new byte[]{7, 6, 11, 15, 12, 5, 14, 3, 9, 10, 0, 8, 13, 1, 4, 2},
            new byte[]{7, 6, 15, 4, 8, 1, 0, 11, 9, 2, 3, 5, 13, 14, 10, 12},
            new byte[]{6, 10, 0, 4, 2, 9, 11, 3, 7, 13, 12, 15, 8, 1, 14, 5},
            new byte[]{2, 6, 9, 10, 4, 7, 1, 5, 8, 3, 13, 12, 15, 11, 0, 14},
            new byte[]{5, 12, 9, 11, 13, 7, 3, 14, 2, 6, 0, 10, 4, 8, 1, 15},
            new byte[]{1, 14, 8, 2, 6, 11, 0, 12, 3, 7, 9, 15, 10, 13, 5, 4},
            new byte[]{7, 14, 0, 8, 10, 13, 3, 6, 15, 5, 4, 11, 12, 2, 9, 1},
            new byte[]{11, 3, 5, 9, 10, 13, 8, 1, 2, 12, 7, 15, 6, 14, 0, 4},
            new byte[]{11, 3, 2, 9, 7, 5, 6, 4, 12, 14, 0, 8, 13, 15, 10, 1},
            new byte[]{15, 12, 9, 13, 10, 1, 2, 0, 3, 11, 8, 6, 14, 4, 5, 7},
            new byte[]{7, 9, 6, 0, 3, 14, 11, 10, 13, 8, 4, 12, 5, 15, 1, 2},
            new byte[]{13, 10, 12, 5, 15, 14, 1, 6, 3, 9, 7, 2, 8, 4, 11, 0},
            new byte[]{10, 3, 5, 8, 4, 2, 9, 15, 7, 11, 13, 0, 14, 6, 12, 1},
            new byte[]{1, 3, 14, 11, 15, 7, 9, 0, 8, 12, 13, 5, 4, 10, 6, 2},
            new byte[]{5, 10, 14, 0, 4, 9, 12, 8, 13, 6, 2, 7, 15, 11, 3, 1},
            new byte[]{6, 15, 8, 7, 2, 12, 3, 11, 1, 10, 9, 5, 4, 13, 14, 0},
            new byte[]{9, 8, 12, 3, 15, 14, 7, 4, 10, 6, 5, 0, 2, 13, 1, 11},
            new byte[]{14, 15, 7, 8, 11, 3, 9, 0, 1, 2, 5, 6, 4, 13, 12, 10},
            new byte[]{9, 10, 4, 5, 0, 14, 13, 6, 7, 12, 11, 8, 15, 2, 1, 3},
            new byte[]{0, 2, 12, 6, 10, 15, 3, 1, 9, 8, 4, 5, 14, 11, 7, 13},
            new byte[]{6, 7, 2, 14, 12, 9, 4, 15, 8, 11, 10, 5, 0, 13, 3, 1},
            new byte[]{10, 5, 12, 13, 6, 3, 15, 1, 0, 4, 9, 11, 2, 14, 8, 7},
            new byte[]{6, 4, 5, 12, 0, 7, 1, 15, 13, 11, 10, 14, 2, 8, 9, 3},
    };

    private static void initAssertConfig() {
    }

    private static void parseConfig(JSONObject jSONObject, String str, byte[][] bArr) {
        JSONArray optJSONArray = jSONObject.optJSONArray(str);
        assert optJSONArray != null;
        if (optJSONArray.length() != 32) {
            TextLogger.INSTANCE.log("config file error!");
            return;
        }
        for (int i2 = 0; i2 < 32; i2++) {
            JSONArray optJSONArray2 = optJSONArray.optJSONArray(i2);
            if (optJSONArray2 == null || optJSONArray2.length() != 16) {
                TextLogger.INSTANCE.log("config file error!");
                return;
            }
            for (int i3 = 0; i3 < 16; i3++) {
                bArr[i2][i3] = (byte) optJSONArray2.optInt(i3);
            }
        }
    }

    public static byte[] transform(int i2, byte[] bArr) {
        try {
            byte[] bArr2 = new byte[bArr.length];
            byte[][] bArr3 = mConfigEnCode;
            if (bArr3.length == 32 && i2 == 1) {
                transformInner(bArr, bArr2, bArr3);
            } else {
                byte[][] bArr4 = mConfigDeCode;
                if (bArr4.length == 32 && i2 == 2) {
                    transformInner(bArr, bArr2, bArr4);
                } else {
                    TextLogger.INSTANCE.log("transform error!");
                }
            }
            return bArr2;
        } catch (Throwable th) {
            TextLogger.INSTANCE.log("encode error!" + th);
            return null;
        }
    }

    private static void transformInner(byte[] bArr, byte[] bArr2, byte[][] bArr3) {
        for (int i2 = 0; i2 < bArr.length; i2++) {
            int i3 = i2 * 2;
            bArr2[i2] = (byte) (bArr3[(i3 + 1) % 32][(byte) (bArr[i2] & 15)] | (bArr3[i3 % 32][(byte) ((bArr[i2] >> 4) & 15)] << 4));
        }
    }
}
