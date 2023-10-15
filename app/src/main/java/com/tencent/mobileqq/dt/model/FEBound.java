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
            new byte[]{15,10,13,4,0,5,3,1,11,12,7,2,14,6,9,8},
            new byte[]{12,1,13,4,14,9,8,6,5,3,10,7,11,2,0,15},
            new byte[]{10,5,14,0,9,1,7,4,11,8,3,15,12,6,13,2},
            new byte[]{7,11,2,14,3,10,1,8,0,15,9,6,13,4,5,12},
            new byte[]{5,15,1,2,4,13,7,8,3,6,11,0,9,10,12,14},
            new byte[]{2,5,0,9,3,15,11,7,8,13,10,4,1,14,6,12},
            new byte[]{15,12,6,14,7,2,0,10,11,13,3,5,1,4,9,8},
            new byte[]{13,2,0,1,8,10,4,14,11,12,7,3,15,9,5,6},
            new byte[]{10,6,7,8,9,3,15,1,2,5,11,12,13,14,0,4},
            new byte[]{8,10,4,2,13,15,12,7,6,3,14,0,1,5,9,11},
            new byte[]{5,7,12,6,0,2,14,3,1,13,9,10,15,11,8,4},
            new byte[]{2,7,12,1,9,10,4,8,13,11,6,3,0,5,15,14},
            new byte[]{0,11,9,3,12,8,14,13,5,4,10,15,7,2,1,6},
            new byte[]{13,1,0,12,6,14,7,11,3,10,2,5,15,8,4,9},
            new byte[]{10,7,5,1,0,6,9,13,14,8,3,15,11,12,4,2},
            new byte[]{7,14,5,13,4,11,15,10,8,0,12,2,3,1,9,6},
            new byte[]{5,2,1,12,10,14,4,15,9,8,6,0,13,11,7,3},
            new byte[]{2,8,6,5,1,3,14,10,0,12,4,13,7,15,9,11},
            new byte[]{0,12,3,11,10,5,4,14,9,7,1,2,13,8,6,15},
            new byte[]{13,3,7,11,4,10,15,0,5,2,6,12,14,9,8,1},
            new byte[]{11,7,4,6,3,0,14,5,2,9,13,15,10,8,12,1},
            new byte[]{8,13,0,11,4,1,3,9,10,15,12,5,14,7,6,2},
            new byte[]{5,3,11,10,13,6,1,15,12,8,2,4,9,14,0,7},
            new byte[]{3,7,9,6,0,5,10,14,1,13,11,4,2,15,8,12},
            new byte[]{0,14,12,15,11,1,3,10,8,2,9,6,13,5,7,4},
            new byte[]{13,4,8,6,3,7,10,0,14,5,9,1,15,12,2,11},
            new byte[]{11,8,13,5,3,14,6,9,1,0,12,15,2,7,10,4},
            new byte[]{8,14,13,10,7,3,0,6,11,12,5,1,15,4,9,2},
            new byte[]{6,2,14,10,15,1,5,8,9,7,11,13,4,3,12,0},
            new byte[]{3,9,2,4,5,8,1,7,11,10,12,0,15,13,6,14},
            new byte[]{0,15,6,14,11,2,1,3,13,4,10,12,7,5,8,9},
            new byte[]{13,5,10,7,2,4,11,0,14,8,1,9,3,12,6,15}
    };
    private static byte[][] mConfigDeCode = {
            new byte[]{13,7,12,2,14,11,3,8,5,9,10,6,1,15,0,4},
            new byte[]{13,8,4,1,9,15,12,2,11,7,10,0,3,5,6,14},
            new byte[]{7,3,9,13,2,6,14,1,5,0,8,4,11,12,10,15},
            new byte[]{9,5,14,13,7,10,0,6,2,15,3,4,11,1,8,12},
            new byte[]{11,1,4,9,0,2,6,10,5,8,14,15,7,3,12,13},
            new byte[]{3,0,12,10,5,13,15,1,11,2,7,4,8,9,6,14},
            new byte[]{1,13,15,12,10,6,11,4,5,3,7,9,14,2,0,8},
            new byte[]{3,8,1,11,6,4,13,10,15,7,2,5,0,14,12,9},
            new byte[]{7,6,13,9,5,14,3,15,1,0,10,4,11,8,2,12},
            new byte[]{7,14,15,5,6,11,9,0,4,10,8,2,1,12,3,13},
            new byte[]{8,5,14,1,0,4,7,13,6,10,15,3,12,9,11,2},
            new byte[]{15,0,5,2,7,3,8,12,11,1,13,4,14,6,9,10},
            new byte[]{12,8,10,6,15,5,14,9,7,3,13,11,2,1,4,0},
            new byte[]{15,6,4,1,2,5,13,11,9,14,3,12,0,10,7,8},
            new byte[]{6,4,8,9,3,12,14,2,10,0,1,15,11,13,5,7},
            new byte[]{9,13,11,12,4,2,15,0,8,14,7,5,10,3,1,6},
            new byte[]{1,4,8,10,0,7,15,9,2,3,14,13,11,6,5,12},
            new byte[]{4,0,10,3,13,7,11,9,5,6,1,14,2,12,15,8},
            new byte[]{3,4,14,8,5,13,10,9,6,2,11,15,12,7,1,0},
            new byte[]{6,10,12,7,4,1,13,8,5,3,11,14,0,2,15,9},
            new byte[]{2,11,14,13,3,9,8,12,4,1,0,15,7,10,5,6},
            new byte[]{6,15,3,4,9,7,11,0,5,14,13,10,12,8,2,1},
            new byte[]{4,14,2,5,0,1,10,7,3,13,6,15,12,8,11,9},
            new byte[]{11,13,0,3,5,14,9,7,4,1,8,15,6,12,10,2},
            new byte[]{11,3,1,8,7,12,6,10,5,9,15,14,2,13,4,0},
            new byte[]{10,13,3,14,15,12,1,2,11,7,4,6,0,5,9,8},
            new byte[]{1,15,5,3,2,6,7,4,8,11,0,14,12,13,10,9},
            new byte[]{1,10,5,7,15,14,12,0,11,8,4,2,3,6,13,9},
            new byte[]{1,2,12,14,8,0,6,10,3,9,7,15,11,4,13,5},
            new byte[]{10,5,0,14,15,13,3,11,8,2,4,1,6,7,12,9},
            new byte[]{8,1,4,12,11,7,9,5,14,6,10,3,13,2,15,0},
            new byte[]{7,10,4,12,5,1,14,3,9,11,2,6,13,0,8,15}
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
