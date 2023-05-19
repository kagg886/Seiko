package com.kagg886.seiko.mock;

import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.util.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.mock
 * @className: UtilsTest
 * @author: kagg886
 * @description: 无
 * @date: 2023/4/6 14:25
 * @version: 1.0
 */
public class UtilsTest {
    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Users\\kagg886\\Desktop\\a.seiko");

        ByteBuffer buffer = ByteBuffer.allocate(10);

        try(FileInputStream stream = new FileInputStream(file)) {
            byte[] a = new byte[4];
            stream.read(a);
            buffer.put(a);
        }
        System.out.printf(Integer.toHexString(buffer.flip().asIntBuffer().get()));
    }
}
