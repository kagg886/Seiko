package com.kagg886.seiko.util;

import android.app.Activity;
import android.content.Intent;
import androidx.core.content.FileProvider;

import java.io.File;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.util
 * @className: ShareUtil
 * @author: kagg886
 * @description: 分享类
 * @date: 2023/1/27 14:46
 * @version: 1.0
 */
public class ShareUtil {
    public static void quickShare(Activity ctx, File p, String type) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(ctx, "com.kagg886.seiko.fileprovider", p));
        intent.setType(type);
        ctx.startActivity(intent);
    }
}
