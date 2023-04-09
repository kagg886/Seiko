package com.kagg886.seiko.mock;

import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        HashMap<String,Object> a = new HashMap<>();
        HashMap<String,Object> b = new HashMap<>();
        b.put("a",1);
        b.put("b",2);
        b.put("c",2);

        a.put("a",1);
        a.put("b",2);
        a.put("c",3);
        a.put("d",b);

        List<Object> c = new ArrayList<>();
        c.add(1);
        c.add(2);
        c.add(3);
        a.put("d",c);
        c.add(b);
        a.put("e",b);
        System.out.println(DictionaryOnRunningException.mapToString(a));
    }
}
