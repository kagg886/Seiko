package com.kagg886.seiko.mock;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.mock
 * @className: UtilsTest
 * @author: kagg886
 * @description: TODO
 * @date: 2023/4/6 14:25
 * @version: 1.0
 */
public class UtilsTest {
    public static void main(String[] args) {
        String a = """
                【 】
                【 】
                【　】""";

        for (String k : a.split("\n")) {
            for (int i = 0; i < k.length(); i++) {
                System.out.print((int) k.charAt(i));
                System.out.print(',');
            }
            System.out.println();
        }
        System.out.println("right:" + (int)' ');
    }
}
