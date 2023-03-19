package com.kagg886.seiko.util;

import org.jsoup.Connection;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class IOUtil {

    private static final FileFilter emptyFilter = pathname -> true;

    public static void zipFile(File src, File dst) throws IOException {
        if (!dst.exists()) {
            dst.createNewFile();
        }
        ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(dst));
        if (src.isDirectory()) {
            zipFile(outputStream, src, src);
        } else {
        }
        outputStream.flush();
        outputStream.close();
    }

    private static void zipFile(ZipOutputStream outputStream, File base, File file) throws IOException {
        if (!file.isDirectory()) {
            outputStream.putNextEntry(new ZipEntry(file.getAbsolutePath().replace(base.getAbsolutePath(), "")));
            outputStream.write(IOUtil.loadByteFromFile(file.getAbsolutePath()));
        } else {
            for (File k : file.listFiles()) {
                zipFile(outputStream, base, k);
            }
        }
    }

    public static void asyncHttp(Connection c, Response resp) {
        new Thread(() -> {
            try {
                byte[] a = IOUtil.loadByteFromStream(c.ignoreContentType(true).execute().bodyStream());
                resp.onSuccess(a);
            } catch (IOException e) {
                resp.onFailed(e);
            }
        }).start();
    }

    public interface Response {
        void onSuccess(byte[] data);

        void onFailed(IOException e);
    }

    public static String getException(Throwable e) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        return writer.toString();
    }

    /*
     * 删除文件
     * */
    public static void delFile(File f) {
        delFile(f,emptyFilter);
    }

    /*
     * @param f:
     * @return void
     * @author kagg886
     * @description 删除文件，但是有过滤器
     * @date 2023/03/13 17:12
     */
    public static void delFile(File f,FileFilter filter) {
        if (!f.isDirectory()) {
            f.delete();
            return;
        }
        for (File t : f.listFiles(filter)) {
            delFile(t);
        }
        f.delete();

    }

    /*
     * 从流中读取所有字节 花费11922ms左右 3.6mb 3万行
     * */

//    public static byte[] loadByteFromStream(InputStream stream) throws IOException {
//        long start = System.currentTimeMillis();
//        ByteArrayOutputStream output = new ByteArrayOutputStream();
//        int by;
//        while ((by = stream.read()) != -1) {
//            output.write(by);
//        }
//
//        stream.close();
//        output.close();
//        System.out.println("第一种方式读取完成");
//        System.out.println(System.currentTimeMillis() - start);
//        return output.toByteArray();
//    }

    public static String loadStringFromStream(InputStream stream) throws IOException {
        return new String(loadByteFromStream(stream));
    }

    /*
     * 尝试高效读取 花费46ms左右 3.6mb 3万行
     */

    //Hamusuta0320 NB
    public static byte[] loadByteFromStream(InputStream is) throws IOException {
        ReadableByteChannel readableByteChannel = Channels.newChannel(is);
        ByteBuffer bf = ByteBuffer.allocate(8192);
        ByteArrayOutputStream sb = new ByteArrayOutputStream();
        int read;
        while ((read = readableByteChannel.read(bf)) != -1) {
            bf.flip();
            sb.write(bf.array(), 0, read);
            bf.clear();
        }
        readableByteChannel.close();
        byte[] res = sb.toByteArray();
        sb.close();
        return res;
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
