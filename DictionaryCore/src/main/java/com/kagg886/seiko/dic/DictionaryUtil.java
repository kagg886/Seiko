package com.kagg886.seiko.dic;

import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.AbsRuntime;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
        } else if (obj instanceof HashMap<?, ?>) {
            qq = Long.parseLong(((HashMap<?, ?>) obj).get("QQ").toString());
            groupId = Long.parseLong(((HashMap<?, ?>) obj).get("所属群").toString());
            botId = Long.parseLong(((HashMap<?, ?>) obj).get("所属BOT").toString());
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

    /*
     * @param runtime: 词库运行时
     * @param arg: 词库表达式
     * @return Object
     * @author kagg886
     * @description 对链式表达式进行求值(不传入尖括号)
     * @date 2023/03/14 22:19
     */
    public static Object chainExpressionCalc(AbsRuntime<?> runtime, String arg) {
        String[] exps = cleanVariableCode(arg, runtime).split("\\.");
        //针对{a[0].%A%}这种情况。要先脱去最外层大括号，然后再执行内层内容。这样可以保证最外层不会被整体替换成字符串。
        //解析后real为a[0].b，可以放心解析
        Object point = runtime.getRuntimeObject();
        for (String str : exps) {
            if (str.contains("(") && str.contains(")")) { //按数组处理
                String arrayIndex = str.substring(str.indexOf("(") + 1, str.length() - 1);
                String arrayName = str.replace("(" + arrayIndex + ")", "");
                point = ((ArrayList<?>) ((HashMap<?, ?>) point).get(arrayName)).get(Integer.parseInt(arrayIndex));
                continue;
            }
            point = ((HashMap<?, ?>) point).get(str);
            if (point == null) {
                throw new DictionaryOnRunningException("找不到变量:" + str);
            }
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
    public static List<Object> variableToObject(String code, AbsRuntime<?> runtime) {
        Object[] args = code.split(" ");
        ArrayList<Object> k = new ArrayList<>();
        for (Object o : args) {
            String arg = (String) o;
            if (arg.startsWith(NORMAL_VARIABLE_SURROUND) && arg.endsWith(NORMAL_VARIABLE_SURROUND)) {//第一步：%A%变量解析
                k.add(runtime.getRuntimeObject().getOrDefault(arg.substring(1, arg.length() - 1), "null"));
                continue;
            }

            if (arg.startsWith(CHAIN_VARIABLE_PREFIX) && arg.endsWith(CHAIN_VARIABLE_SUFFIX)) { //第二步：<A>变量解析
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
     * @description 将形如%A%的变量和<a.b.c>或<a(0).b>转换成字符串
     * @date 2023/01/13 09:44
     */
    public static String cleanVariableCode(String code, AbsRuntime<?> runtime) {
        String clone = code.replace("\\n", "\n");

        //第一步：解析增强表达式。把这一步放在前面是为了防止基础表达式展开为集合对象时对此步造成的干扰
        int lIndex;
        int rIndex = 0;

        //这里一定要加accessPoint限定，不然会死循环
        int errorPoint = 0;
        while ((lIndex = clone.indexOf(CHAIN_VARIABLE_PREFIX,errorPoint)) != -1 && (rIndex = clone.indexOf(CHAIN_VARIABLE_SUFFIX, lIndex)) != -1) { //防止出现括号不匹配的情况发生
            try {
                Object point = chainExpressionCalc(runtime,clone.substring(lIndex + CHAIN_VARIABLE_PREFIX_OFFSET, rIndex));
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
                Object q = runtime.getRuntimeObject().get(s);
                if (q == null) {
                    q = "null";
                }
                clone = clone.replace(var, q.toString());
            }
        }

        try {
            //第三步：计算表达式，若出错则不计算
            int xLeft = 0;
            while ((xLeft = clone.indexOf("[", xLeft)) != -1) {
                int xRight = clone.indexOf("]", xLeft);
                String expression = clone.substring(xLeft + 1, xRight);
                String result = String.valueOf(DictionaryUtil.mathExpressionCalc(expression));
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
    public static boolean evalBooleanExpression(String str) {
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
            boolean p = evalBooleanExpression(str.substring(lIndex + 1, rIndex));
            return evalBooleanExpression(str.replace("(" + str.substring(lIndex + 1, rIndex) + ")", Boolean.toString(p)));
        }

        if (str.contains("||")) {
            int idx = str.indexOf("||");
            return evalBooleanExpression(str.substring(0, idx)) || evalBooleanExpression(str.substring(idx + 2));
        }

        if (str.contains("&&")) {
            int idx = str.indexOf("&&");
            return evalBooleanExpression(str.substring(0, idx)) && evalBooleanExpression(str.substring(idx + 2));
        }

        if (str.contains("==")) {
            int idx = str.indexOf("==");
            try {
                return Objects.equals(mathExpressionCalc(str.substring(0, idx)), mathExpressionCalc(str.substring(idx + 2)));
            } catch (Exception e) {
                // 代表等式左边或右边是字符串，按照字符串进行匹配
                return str.substring(0, idx).equals(str.substring(idx + 2));
            }
        }

        if (str.contains("!=")) {
            int idx = str.indexOf("!=");
            try {
                return !Objects.equals(mathExpressionCalc(str.substring(0, idx)), mathExpressionCalc(str.substring(idx + 2)));
            } catch (Exception e) {
                return !str.substring(0, idx).equals(str.substring(idx + 2));
            }
        }
        if (str.contains(">=")) {
            int idx = str.indexOf(">=");
            return mathExpressionCalc(str.substring(0, idx)) >= mathExpressionCalc(str.substring(idx + 2));
        }
        if (str.contains("<=")) {
            int idx = str.indexOf("<=");
            return mathExpressionCalc(str.substring(0, idx)) <= mathExpressionCalc(str.substring(idx + 2));

        }
        if (str.contains(">")) {
            int idx = str.indexOf(CHAIN_VARIABLE_SUFFIX);
            return mathExpressionCalc(str.substring(0, idx)) > mathExpressionCalc(str.substring(idx + 1));
        }
        if (str.contains("<")) {
            int idx = str.indexOf(CHAIN_VARIABLE_PREFIX_OFFSET);
            return mathExpressionCalc(str.substring(0, idx)) < mathExpressionCalc(str.substring(idx + 1));

        }
        throw new DictionaryOnRunningException("计算表达式出错!" + str);
    }


    public static Double mathExpressionCalc(String str) {
        if (str.equals("")) {
            throw new DictionaryOnRunningException("中括号内不能为空");
        }
        Double a = null;
        try {
            a = Double.parseDouble(str);
        } catch (NumberFormatException ignored) {
        }

        if (str.isEmpty() || a != null) {
            return str.isEmpty() ? 0 : a;
        }

        if (str.contains(")")) {
            // 最后一个左括号
            int lIndex = str.lastIndexOf("(");
            // 对于的右括号
            int rIndex = str.indexOf(")", lIndex);
            return mathExpressionCalc(str.substring(0, lIndex) + mathExpressionCalc(str.substring(lIndex + 1, rIndex)) + str.substring(rIndex + 1));
        }
        if (str.contains("+")) {
            int index = str.lastIndexOf("+");
            return mathExpressionCalc(str.substring(0, index)) + mathExpressionCalc(str.substring(index + 1));
        }
        if (str.contains("-")) {
            int index = str.lastIndexOf("-");
            return mathExpressionCalc(str.substring(0, index)) - mathExpressionCalc(str.substring(index + 1));
        }
        if (str.contains("*")) {
            int index = str.lastIndexOf("*");
            return mathExpressionCalc(str.substring(0, index)) * mathExpressionCalc(str.substring(index + 1));
        }
        if (str.contains("/")) {
            int index = str.lastIndexOf("/");
            return mathExpressionCalc(str.substring(0, index)) / mathExpressionCalc(str.substring(index + 1));
        }

        if (str.contains("^")) {
            int index = str.lastIndexOf("^");
            return Math.pow(mathExpressionCalc(str.substring(0, index)), mathExpressionCalc(str.substring(index + 1)));
        }

        if (str.contains("%")) {
            int index = str.lastIndexOf("%");
            return mathExpressionCalc(str.substring(0, index)) % mathExpressionCalc(str.substring(index + 1));
        }
        // 出错
        throw new DictionaryOnRunningException("无法解析的表达式:" + str);
    }
}
