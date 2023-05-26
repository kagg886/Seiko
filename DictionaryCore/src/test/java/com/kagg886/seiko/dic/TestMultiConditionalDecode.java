package com.kagg886.seiko.dic;

import com.kagg886.seiko.dic.entity.DictionaryCode;
import com.kagg886.seiko.dic.entity.impl.ConditionalExpression;
import com.kagg886.seiko.dic.entity.impl.PlainText;
import com.kagg886.seiko.util.ArrayIterator;
import com.kagg886.seiko.util.TextUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试多重嵌套如果表达式
 *
 * @author kagg886
 * @date 2023/5/26 15:57
 **/
public class TestMultiConditionalDecode {

    List<DictionaryCode> insertArray(ArrayIterator<String> root, int deep) {
        List<DictionaryCode> func = new ArrayList<>();
        while (root.hasNext()) {
            String comm = root.next();

            String prefix = TextUtils.repeat(" ", deep);
            if (comm.startsWith(prefix)) { //符合深度，开始填充
                comm = comm.replace(prefix, ""); //解空格
                if (comm.startsWith("如果:")) {
                    ConditionalExpression expression = new ConditionalExpression(root.getLen(), comm);
                    expression.setSuccess(insertArray(root, deep + 1));

                    root.setLen(root.getLen() - 1); //这一步是回滚进度，因为iterator方法最坏都会向后执行一步
                    comm = root.next(); //提前获取下一步指令是如果尾还是闭合标志
                    if (comm.startsWith(prefix + "如果尾")) {
                        expression.setFailed(insertArray(root, deep + 1));
                    }
                    func.add(expression);
                    root.setLen(root.getLen() - 1);
                } else {
                    //解析其他地方的表达式
                    func.add(new PlainText(root.getLen(), comm));
                }
            } else {
                return func;
            }
        }
        return func;
     }

    @Test
    void test() {
        ArrayIterator<String> string = new ArrayIterator<>(decode.split("\n"));
        List<DictionaryCode> code = insertArray(string, 0);
        code.forEach(System.out::println);
    }

    private static final String decode =
            "如果:%A%==1\n" +
                    " 语句1\n" +
                    " 语句2\n" +
                    " 如果:%A%==2\n" +
                    "  语句3\n" +
                    "  语句4\n" +
                    "  如果:%A%==3\n" +
                    "   语句5\n" +
                    "   语句6\n" +
                    "  语句7\n" +
                    "  语句8\n" +
                    " 如果尾\n" +
                    "  语句9\n" +
                    "  语句10\n" +
                    "语句11\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n";
}
