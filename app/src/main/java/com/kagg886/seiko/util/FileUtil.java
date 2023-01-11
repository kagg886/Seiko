package com.kagg886.seiko.util;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
    public static void saveFile(Context ctx, byte[] data, File dst) throws IOException {
        if(!dst.exists()) {
            dst.createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(dst);
        fileOutputStream.write(data);
        fileOutputStream.close();
    }
}
