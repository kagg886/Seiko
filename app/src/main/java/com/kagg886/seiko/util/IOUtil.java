package com.kagg886.seiko.util;

import android.app.Activity;
import android.content.Intent;
import androidx.core.content.FileProvider;

import java.io.*;

public class IOUtil {

    public static String getException(Throwable e) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        return writer.toString();
    }

    public static void quickShare(Activity ctx, File p, String type) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(ctx, "com.kagg886.seiko.fileprovider", p));
        intent.setType(type);
        ctx.startActivity(intent);
    }

    public interface Response {
        void onSuccess(byte[] byt) throws Exception;

        void onFailed(Throwable t);
    }

    /*
     * 删除文件
     * */
    public static void delFile(File f) {
        if (!f.isDirectory()) {
            f.delete();
            return;
        }
        for (File t : f.listFiles()) {
            delFile(t);
        }
        f.delete();
    }

    /*
     * 从流中读取所有字节
     * */

    public static byte[] loadByteFromStream(InputStream stream) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int by;
        while ((by = stream.read()) != -1) {
            output.write(by);
        }

        stream.close();
        output.close();

        return output.toByteArray();
    }

    public static String loadStringFromStream(InputStream stream) throws IOException {
        return new String(loadByteFromStream(stream));
    }



    /*
     *读取文件
     * */

    public static byte[] loadByteFromFile(String file) throws IOException {
        FileInputStream stream;
        try {
            stream = new FileInputStream(file);
        } catch (Exception e) {
            return new byte[0];
        }
        return loadByteFromStream(stream);
    }

    public static String loadStringFromFile(String file) throws IOException {
        return new String(loadByteFromFile(file));
    }


    public static void writeByteToFile(String file, byte[] byt) throws IOException {
        FileOutputStream stream;
        try {
            stream = new FileOutputStream(file);
        } catch (Exception e) {
            File b = new File(file);
            b.getParentFile().mkdirs();
            if (!b.createNewFile()) {
                throw e;
            }
            writeByteToFile(file, byt);
            return;
        }
        stream.write(byt);
        stream.close();
    }

    public static void writeStringToFile(String file, String content) throws IOException {
        writeByteToFile(file, content.getBytes());
    }

    public static boolean copyFile(File config, File f) {
        try {
            writeByteToFile(f.getAbsolutePath(), loadByteFromFile(config.getAbsolutePath()));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
