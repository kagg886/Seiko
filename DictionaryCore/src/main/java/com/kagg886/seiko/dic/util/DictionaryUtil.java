package com.kagg886.seiko.dic.util;

import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.AbsRuntime;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic
 * @className: DictionaryUtil
 * @author: kagg886
 * @description: 伪代码常用操作
 * @date: 2023/1/16 19:06
 * @version: 1.0
 */
public class DictionaryUtil {

    private static final String NORMAL_VARIABLE_SURROUND = "%";

    private static final String CHAIN_VARIABLE_PREFIX = "{";

    private static final int CHAIN_VARIABLE_PREFIX_OFFSET = CHAIN_VARIABLE_PREFIX.length();

    private static final String CHAIN_VARIABLE_SUFFIX = "}";

    public static Group getGroupByObjectList(AbsRuntime<?> runtime, List<Object> args, int start) {
        long groupId, botAccount;

        Object obj = args.get(start); //群号或上下文
        if (obj instanceof GroupMessageEvent) {
            groupId = ((GroupMessageEvent) obj).getGroup().getId();
            botAccount = ((GroupMessageEvent) obj).getBot().getId();
        } else {
            groupId = Long.parseLong(obj.toString());
            if (args.size() >= start + 2) {
                botAccount = Long.parseLong(args.get(start + 1).toString());
            } else {
                botAccount = Long.parseLong(runtime.getRuntimeObject().get("BOT").toString());
            }
        }
        return Objects.requireNonNull(Bot.findInstance(botAccount)).getGroup(groupId);
    }

    /*
     * @param runtime:
     * @param args:
     * @param start:
     * @return Info
     * @author kagg886
     * @description 根据输入参数解析群成员方法,格式为:$XXX QQ 群号 bot号$ 或 $XXX %上下文%$ 或 $XXX %集合%$
     * @date 2023/03/12 18:43
     */
    public static NormalMember getMemberInfoByObjectList(AbsRuntime<?> runtime, List<Object> args, int start) {
        long qq, groupId, botId;
        Object obj = args.get(start); //qq或上下文
        if (obj instanceof GroupMessageEvent) {
            qq = ((GroupMessageEvent) obj).getSender().getId();
            groupId = ((GroupMessageEvent) obj).getGroup().getId();
            botId = ((GroupMessageEvent) obj).getBot().getId();
        } else if (obj instanceof Map<?, ?>) {
            qq = Long.parseLong(((Map<?, ?>) obj).get("QQ").toString());
            groupId = Long.parseLong(((Map<?, ?>) obj).get("所属群").toString());
            botId = Long.parseLong(((Map<?, ?>) obj).get("所属BOT").toString());
        } else {
            qq = Long.parseLong(obj.toString());
            if (args.size() >= start + 2) {
                groupId = Long.parseLong(args.get(start + 1).toString());
                if (args.size() >= start + 3) {
                    botId = Long.parseLong(args.get(start + 2).toString());
                } else {
                    botId = ((GroupMessageEvent) runtime.getRuntimeObject().get("上下文")).getBot().getId();
                }
            } else {
                groupId = ((GroupMessageEvent) runtime.getRuntimeObject().get("上下文")).getGroup().getId();
                botId = ((GroupMessageEvent) runtime.getRuntimeObject().get("上下文")).getBot().getId();
            }
        }
        return Bot.findInstance(botId).getGroup(groupId).get(qq);
    }

    ////链式表达式求值的核心函数，对每个单元进行求值
    private static Object chainExpressionCalc0(Object point, String str) {
        Object rtn;
        if (str.contains("::")) { //伪元素用伪元素专用的获取方式
            String[] suffix = str.split("::");
            rtn = chainExpressionCalc0(point, suffix[0]);
            for (int i = 1; i < suffix.length; i++) {
                //伪元素解析
                if (suffix[i].equals("len")) {
                    if (rtn instanceof Map<?, ?>) {
                        point = ((Map<?, ?>) rtn).size();
                        break;
                    }
                    if (rtn instanceof List<?>) {
                        point = ((List<?>) rtn).size();
                        break;
                    }
                }

                if (suffix[i].startsWith("kArr")) {
                    point = new ArrayList<>(((Map<?, ?>) rtn).keySet());
                    //考虑kArr(0)的情况，此时值应该是kArr里的一个元素。vArr同理
                    if (suffix[i].contains("(") && suffix[i].contains(")")) {
                        String arrayIndex = suffix[i].substring(suffix[i].indexOf("(") + 1, suffix[i].length() - 1);
                        List<?> list = (List<?>) point;
                        int idx = Integer.parseInt(arrayIndex);
                        point = idx < list.size() ? list.get(idx) : null;
                    }
                }

                if (suffix[i].startsWith("vArr")) {
                    point = new ArrayList<>(((Map<?, ?>) rtn).values());
                    if (suffix[i].contains("(") && suffix[i].contains(")")) {
                        String arrayIndex = suffix[i].substring(suffix[i].indexOf("(") + 1, suffix[i].length() - 1);
                        List<?> list = (List<?>) point;
                        int idx = Integer.parseInt(arrayIndex);
                        point = idx < list.size() ? list.get(idx) : null;
                    }
                }
            }
            rtn = point;
        } else { //单元素解析，传统代码
            if (str.contains("(") && str.contains(")")) { //按数组处理
                String arrayIndex = str.substring(str.indexOf("(") + 1, str.length() - 1);
                String arrayName = str.replace("(" + arrayIndex + ")", "");

                List<?> list = ((List<?>) ((Map<?, ?>) point).get(arrayName));
                int idx = Integer.parseInt(arrayIndex);
                rtn = idx < list.size() ? list.get(idx) : null;
            } else {
                rtn = ((Map<?, ?>) point).getOrDefault(str, null);
            }
        }
        return rtn;
    }

    /*
     * @param runtime: 词库运行时
     * @param arg: 词库表达式
     * @return Object
     * @author kagg886
     * @description 对链式表达式进行求值(不传入尖括号)
     * @date 2023/03/14 22:19
     */
    public static Object chainExpressionCalc(AbsRuntime<?> runtime, String arg) {
        //针对{a[0].%A%}这种情况。要先脱去最外层大括号，然后再执行内层内容。这样可以保证最外层不会被整体替换成字符串。
        //解析后real为a[0].b，可以放心解析
        return chainExpressionCalc1(runtime.getRuntimeObject(), cleanVariableCode(arg, runtime));
    }

    //链式表达式求值的核心函数，此处负责根据英文句号分块求值
    private static Object chainExpressionCalc1(Object point, String arg) {
        String[] exps = arg.split("\\.");
        int checkCount = 0; //使用checkCount判断根变量是否为null。是则直接抛出异常
        for (String str : exps) {
            if (point == null) { //上一个已经为null了，直接抛异常
                throw new IllegalArgumentException("找不到键:" + exps[Arrays.binarySearch(exps, str) - 1]);
            }
            point = chainExpressionCalc0(point, str);
            checkCount++;
        }
        if (point == null) {
            if (checkCount == 1) {
                throw new IllegalArgumentException("变量不存在:" + exps[0]);
            }
            point = "null";
        }
        return point;
    }

    /*
     * @param code:参数表
     * @param runtime:运行时
     * @return Object
     * @author kagg886
     * @description 用于函数，将参数变量提取成字符串。
     * @date 2023/01/19 18:51
     */
    public static List<Object> variableToObject(String[] args, AbsRuntime<?> runtime) {
        ArrayList<Object> k = new ArrayList<>();
        for (String arg : args) {
            if (arg.startsWith(NORMAL_VARIABLE_SURROUND) && arg.endsWith(NORMAL_VARIABLE_SURROUND)) {//第一步：%A%变量解析
                k.add(runtime.getRuntimeObject().getOrDefault(arg.substring(1, arg.length() - 1), "null"));
                continue;
            }

            if (arg.startsWith(CHAIN_VARIABLE_PREFIX) && arg.endsWith(CHAIN_VARIABLE_SUFFIX)) { //第二步：{A}变量解析
                //使用subString，防止表达式本身被解析
                try {
                    k.add(chainExpressionCalc(runtime, cleanVariableCode(arg.substring(1, arg.length() - 1), runtime)));
                } catch (Exception ignored) {
                    //这可能不是表达式，跳过解析
                    k.add(arg);
                }
                continue;
            }
            k.add(cleanVariableCode(arg, runtime));
        }
        return k;
    }

    /*
     * @param :
     * @return String
     * @author kagg886
     * @description 将形如%A%的变量和{a.b.c}或{a(0).b}转换成字符串
     * @date 2023/01/13 09:44
     */
    public static String cleanVariableCode(String code, AbsRuntime<?> runtime) {
        String clone = code.replace("\\n", "\n");

        //第一步：解析增强表达式。把这一步放在前面是为了防止基础表达式展开为集合对象时对此步造成的干扰
        int lIndex;
        int rIndex;

        //这里一定要加accessPoint限定，不然会死循环
        int errorPoint = 0;
        while ((lIndex = clone.indexOf(CHAIN_VARIABLE_PREFIX, errorPoint)) != -1 && (rIndex = clone.indexOf(CHAIN_VARIABLE_SUFFIX, lIndex)) != -1) { //防止出现括号不匹配的情况发生
            try {
                Object point = chainExpressionCalc(runtime, clone.substring(lIndex + CHAIN_VARIABLE_PREFIX_OFFSET, rIndex));
                clone = clone.replace(clone.substring(lIndex, rIndex + 1), point.toString());
            } catch (Exception ignored) {
                //表达式解析失败，可能是字符串。跳过本次进行下一次解析。不能break，因为不知道解析是否已完成
                errorPoint = rIndex;
            }
        }

        //第二步：解析基础表达式
        for (String s : runtime.getRuntimeObject().keySet()) { //s一定是String
            String var = NORMAL_VARIABLE_SURROUND + s + NORMAL_VARIABLE_SURROUND;
            if (clone.contains(var)) {
                Object val = runtime.getRuntimeObject().getOrDefault(s, null);
                if (val == null) { //不排除有脑瘫手动往里面加null值
                    val = "null";
                }
                clone = clone.replace(var, val.toString());
            }
        }

        try {
            //第三步：计算表达式，若出错则不计算
            int xLeft = 0;
            while ((xLeft = clone.indexOf("[", xLeft)) != -1) {
                int xRight = clone.indexOf("]", xLeft);
                String expression = clone.substring(xLeft + 1, xRight);
                //String result = String.valueOf(DictionaryUtil.mathExpressionCalc(expression));
                String result;
                try {
                    result = new BigDecimal(Double.toString(DictionaryUtil.mathExpressionCalc(expression))).toPlainString();
                } catch (NumberFormatException e) {
                    result = "NaN";
                }
                if (result.endsWith(".0")) { //小数整数化
                    result = result.substring(0, result.length() - 2);
                }
                clone = clone.replace("[" + expression + "]", result);
                xLeft = xRight;
            }
        } catch (Exception ignored) {
        }

        return clone;
    }


    /*
     * @param str: 传入的布尔表达式
     * @return boolean
     * @author kagg886
     * @description 评估布尔表达式
     * @date 2023/01/28 21:33
     */
    public static boolean evalBooleanExpression(String str, AbsRuntime<?> runtime) {
        if (str == null || str.equals("")) {
            throw new NullPointerException("表达式为空");
        }
        str = str.replace(" ", "");

        if (str.equals("true")) {
            return true;
        }

        if (str.equals("false")) {
            return false;
        }

        if (str.contains(")")) {
            int lIndex = str.lastIndexOf("(");
            int rIndex = str.indexOf(")", lIndex);
            boolean p = evalBooleanExpression(str.substring(lIndex + 1, rIndex), runtime);
            return evalBooleanExpression(str.replace("(" + str.substring(lIndex + 1, rIndex) + ")", Boolean.toString(p)), runtime);
        }

        if (str.contains("||")) {
            int idx = str.indexOf("||");
            return evalBooleanExpression(str.substring(0, idx), runtime) || evalBooleanExpression(str.substring(idx + 2), runtime);
        }

        if (str.contains("&&")) {
            int idx = str.indexOf("&&");
            return evalBooleanExpression(str.substring(0, idx), runtime) && evalBooleanExpression(str.substring(idx + 2), runtime);
        }

        Function<String,Double> varCalc = (deal) -> mathExpressionCalc(DictionaryUtil.cleanVariableCode(deal, runtime));

        if (str.contains("==")) {
            int idx = str.indexOf("==");
            try {
                return Objects.equals(varCalc.apply(str.substring(0, idx)), varCalc.apply(str.substring(idx + 2)));
            } catch (Exception e) {
                // 代表等式左边或右边是字符串，按照字符串进行匹配
                return DictionaryUtil.cleanVariableCode(str.substring(0, idx), runtime).equals(DictionaryUtil.cleanVariableCode(str.substring(idx + 2), runtime));
            }
        }

        if (str.contains("!=")) {
            int idx = str.indexOf("!=");
            try {
                return !Objects.equals(varCalc.apply(str.substring(0, idx)), varCalc.apply(str.substring(idx + 2)));
            } catch (Exception e) {
                return !DictionaryUtil.cleanVariableCode(str.substring(0, idx), runtime).equals(DictionaryUtil.cleanVariableCode(str.substring(idx + 2), runtime));
            }
        }


        if (str.contains(">=")) {
            int idx = str.indexOf(">=");
            return varCalc.apply(str.substring(0, idx)) >= varCalc.apply(str.substring(idx + 2));
        }
        if (str.contains("<=")) {
            int idx = str.indexOf("<=");
            return varCalc.apply(str.substring(0, idx)) <= varCalc.apply(str.substring(idx + 2));

        }
        if (str.contains(">")) {
            int idx = str.indexOf(">");
            return varCalc.apply(str.substring(0, idx)) > varCalc.apply(str.substring(idx + 1));
        }
        if (str.contains("<")) {
            int idx = str.indexOf("<");
            return varCalc.apply(str.substring(0, idx)) >= varCalc.apply(str.substring(idx + 1));

        }
        throw new DictionaryOnRunningException("计算表达式出错!" + str);
    }

    public static Double mathExpressionCalc(String str) {
        return new ExpressionBuilder(str).build().evaluate();
    }
}


/*
嵌套括号表达式求值探究
package com.kagg886;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
public class Main {
    public static void main(String[] args) throws Exception {
        String regex = "{a.b(3).{f.b.i}.d({c.n.m}).e.f({q.{s.b(2).n}.p}).g(2).i.j.k.l.m.n}";
        DeepStack stack = new DeepStack();

        for (int i = 0; i < regex.length(); i++) {
            char a = regex.charAt(i);
            if (a == '{' || a == '}') {
                stack.push(a,i);
            }
        }

        List<RegexItem> sort = stack.getResult().stream()
                .sorted(Comparator.comparingInt(o -> o.deep))
                .collect(Collectors.toList());
        for (RegexItem i : sort) {
            System.out.printf("第%d层:%s\n",i.deep,regex.substring(i.lIndex+1, i.rIndex));
        }
    }

    static class DeepStack extends Stack<CharItem> {

        private ArrayList<RegexItem> result = new ArrayList<RegexItem>();
        private int deep = 0;

        public ArrayList<RegexItem> getResult() {
            if (size()!=0) {
                throw new RuntimeException();
            }
            return result;
        }

        public void push(char chr, int index) {
            if (size() == 0) {
                super.push(new CharItem(chr,index));
                deep++;
                return;
            }
            CharItem charItem = peek();
            if (chr == '}' && charItem.chr == '{') {
                pop();
                result.add(new RegexItem(charItem.index,index,deep--));
                return;
            }

            super.push(new CharItem(chr,index));
            deep++;
        }
    }

    static class RegexItem {
        private int lIndex;
        private int rIndex;
        private int deep;

        public RegexItem(int lIndex,int rIndex,int deep) {
            this.deep = deep;
            this.rIndex = rIndex;
            this.lIndex = lIndex;
        }
    }

    static class CharItem {
        public int index;
        public char chr;

        public CharItem(char chr,int index) {
            this.index = index;
            this.chr = chr;
        }
    }
}

 */
