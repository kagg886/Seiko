package com.kagg886.seiko.dic.util;


import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Erzbir
 * @Date: 2023/4/5 14:25
 */
public class Calculator {

    private final Stack<Double> numStack = new Stack<>();
    private final Stack<Character> opStack = new Stack<>();

    private char currentOperator;

    private int i;
    private String expression;

    Double dllll = 1.1;

    private static class TriFuncCalculator {
        public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
            String s = "log(tan(sin(cos(12))))";
            System.out.println(cal(s));
        }

        public static boolean verify(String exp) {
            return Pattern.compile("\\(?[a-z]+(.+)\\)?").matcher(exp).matches();
        }

        public static double cal(String exp) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
            return cal2(parseExToList(exp));
        }

        private static double cal2(LinkedList<String> list) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            Class<?> uClass = Util.class;
            LinkedList<String> tempList = (LinkedList<String>) list.clone();
            while (list.size() > 0) {
                String pop = String.valueOf(list.pollLast());
                Pattern pattern = Pattern.compile("([a-z]+)\\((.+)\\)");
                Matcher matcher = pattern.matcher(pop);
                if (!matcher.matches()) {
                    continue;
                }
                //  System.out.println(pop);
                double result = (double) uClass.getDeclaredMethod(matcher.group(1), double.class)
                        .invoke(null, Double.parseDouble(matcher.group(2)));
                // System.out.println(result);
                String s = list.peekLast();
                if (s != null) {
                    String replace = s.replace(Objects.requireNonNull(tempList.pollLast()), String.valueOf(result));
                    list.set(list.size() - 1, replace);
                } else {
                    return result;
                }
            }
            return Double.NaN;
        }

        private static LinkedList<String> parseExToList(String ex) {
            LinkedList<String> list = new LinkedList<>();
            list.addFirst(ex.replace("(.+)", ""));
            parseExToList2(ex, list);
            list.removeLast();
            return list;
        }

        private static void parseExToList2(String ex, LinkedList<String> list) {
            String s = "\\(?[a-z]+(.+)\\)?";
            Pattern pattern = Pattern.compile(s);
            Matcher matcher = pattern.matcher(ex);
            byte size = 0;
            if (!matcher.matches()) {
                return;
            }
            while (true) {
                try {
                    matcher.group(size++);
                } catch (Exception ignore) {
                    size--;
                    break;
                }
            }
            if (size <= 2 && !pattern.matcher(matcher.group(1)).matches()) {
                list.addLast(matcher.group());
            } else {
                String substring = matcher.group(1).replaceFirst("\\(", "").substring(0, matcher.group(1).length() - 2);
                list.addLast(substring.replace("\\(?(.+)\\)?", ""));
                parseExToList2(substring, list);
            }
        }

        @SuppressWarnings("unused")
        private static class Util {
            public static double ln(double x) {
                return Math.log(x);
            }

            public static double log(double x) {
                return Math.log10(x);
            }

            public static double tan(double x) {
                return Math.tan(x);
            }

            public static double sin(double x) {
                return Math.sin(x);
            }

            public static double cos(double x) {
                return Math.cos(x);
            }
        }
    }


    @SuppressWarnings("rawtypes")
    public String exec(String expression) {
        try {
            clean();
            if (expression == null || expression.isEmpty()) {
                throw new IllegalArgumentException("Blank Expression!");
            }
            this.expression = expression;
            opStack.push(TERMINATE_TOKENS.START_END_MARK);
            List tokens = TOKENIZER.exec(expression
                    + TERMINATE_TOKENS.START_END_MARK);
            System.out.println(tokens);
            for (; i < tokens.size(); i++) {
                final Object token = tokens.get(i);
                if (token instanceof Double) {
                    processOperand((double) token);
                } else {
                    dllll = processOperator((char) token);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            System.err.printf(
                    "Incorret Expression: %s\nError: %s%n", expression,
                    e.getMessage());
        }
        return String.valueOf(dllll);
    }

    private void processOperand(final double operand) {
        numStack.push(operand);
    }

    private Double processOperator(final char currentOperator) {
        this.currentOperator = currentOperator;
        char opStackTop = opStack.peek();
        char calMode = CALCULATE_MODE.getRule(currentOperator, opStackTop);

        System.out.println(calMode);
        switch (calMode) {
            case '>' -> processStackHigherPriorityOperator();
            case '<' -> processStackLowerPriorityOperator();
            case '=' -> {
                return processStackEqualPriorityOperator();
            }
            default -> {
            }
        }
        return 0.0;
    }

    private void processStackLowerPriorityOperator() {
        opStack.push(currentOperator);
    }

    private void processStackHigherPriorityOperator() {
        numStack.push(CALCULATE.exec(opStack.pop(), numStack.pop(),
                numStack.pop()));
        --i; // pointer back to the previous operator.
    }

    private Double processStackEqualPriorityOperator() {
        if (TERMINATE_TOKENS.START_END_MARK == currentOperator) {
            System.out.println(expression + " ===== " + numStack.peek());
            return numStack.peek();
        } else if (')' == currentOperator) {
            opStack.pop();
            return 0.0;
        }
        return 0.0;
    }

    public void clean() {
        numStack.clear();
        opStack.clear();
        i = 0;
    }

    public static void main(String[] args) {
        Calculator cal = new Calculator();


        System.out.println((cal.exec("4+(1+3*18+2)/2"))); // = 32.5
        /* Calculator cal = new Calculator();
         cal.exec("4+(3*(3-1)+2)/2"); // = 8
         cal.exec("4 + (-3 * ( 3 - 1 ) + 2)"); // = 0
         cal.exec("4 +-/ (-3 * ( 3 - 1 ) + 2)"); // incorrect expression!
         cal.exec("4.5+(3.2+3)/2"); // = 7.6
         cal.exec("4.5+(3.2:3)/2"); // incorrect expression!
         cal.exec("-4.5+(3.2-3)/2"); // = -4.4*/
    }
}

enum CALCULATE {
    INSTANCE;

    public static double exec(final char operator, final double right,
                              final double left) {

        BigDecimal a, b;

        a = new BigDecimal(left);

        b = new BigDecimal(right);

        BigInteger a2, b2;

        a2 = a.toBigInteger();

        b2 = b.toBigInteger();

        return switch (operator) {
            case '+' -> a.add(b).doubleValue();
            case '-' -> a.subtract(b).doubleValue();
            case '*' -> a.multiply(b).doubleValue();
            case '/' -> a.divide(b, 10, RoundingMode.DOWN).doubleValue();
            case '%' -> a2.mod(b2).doubleValue();
            case '&' -> a2.and(b2).doubleValue();
            case '|' -> a2.or(b2).doubleValue();
            case '^' -> a2.xor(b2).doubleValue();
            default -> throw new IllegalArgumentException("Unsupported operator: "
                    + operator);
        };
    }

}

enum TERMINATE_TOKENS {
    INSTANCE;

    public static final char START_END_MARK = '#';
    private static final Map<Character, Integer> TOKENs = new HashMap<>();

    static {
        // token, token id
        TOKENs.put('+', 0);
        TOKENs.put('-', 1);
        TOKENs.put('*', 2);
        TOKENs.put('/', 3);

        TOKENs.put('%', 4);
        TOKENs.put('&', 5);
        TOKENs.put('|', 6);
        TOKENs.put('^', 7);


        TOKENs.put('(', 8);
        TOKENs.put(')', 9);
        TOKENs.put(START_END_MARK, 10);
    }

    private static final Set<Character> NEGATIVE_NUM_SENSITIVE = new HashSet<>();

    public static synchronized Set<Character> getNegativeNumSensitiveToken() {
        if (NEGATIVE_NUM_SENSITIVE.size() == 0) {
            NEGATIVE_NUM_SENSITIVE.addAll(TOKENs.keySet());
            NEGATIVE_NUM_SENSITIVE.remove(')');
        }
        return NEGATIVE_NUM_SENSITIVE;
    }

    public static boolean isTerminateToken(final char token) {
        Set<Character> keys = TOKENs.keySet();
        return keys.contains(token);
    }

    public static int getTokenId(final char token) {
        return TOKENs.get(token) == null ? -1 : TOKENs.get(token);
    }

    public static int getTokenSize() {
        return TOKENs.size();
    }

}

enum CALCULATE_MODE {
    INSTANCE;

    private static final char[][] RULES = {
            // + - * / ( ) #
            {'>', '>', '<', '<', '<', '<', '<', '<', '<', '>', '>'}, // +
            {'>', '>', '<', '<', '<', '<', '<', '<', '<', '>', '>'}, // -

            {'>', '>', '<', '<', '<', '<', '<', '<', '<', '>', '>'}, // *
            {'>', '>', '<', '<', '<', '<', '<', '<', '<', '>', '>'}, // /

            {'>', '>', '<', '<', '<', '<', '<', '<', '<', '>', '>'}, // %
            {'>', '>', '<', '<', '<', '<', '<', '<', '<', '>', '>'}, // &
            {'>', '>', '<', '<', '<', '<', '<', '<', '<', '>', '>'}, // |
            {'>', '>', '<', '<', '<', '<', '<', '<', '<', '>', '>'}, // ^


            {'<', '<', '<', '<', '<', '<', '<', '<', '<', '=', 'o'}, // (
            {'>', '>', '>', '>', '>', '>', '>', '>', 'o', '>', '>'}, // )
            {'<', '<', '<', '<', '<', '<', '<', '<', '<', 'o', '='}, // #
    };

    static {
        if (RULES.length != TERMINATE_TOKENS.getTokenSize()) {
            throw new IllegalArgumentException("Rules matrix is incorrect!");
        }
    }

    public static char getRule(final char currentOperator, final char opStackTop) {
        try {
            System.out.println(opStackTop + "=" + currentOperator);

            System.out.println("(" + TERMINATE_TOKENS.getTokenId(opStackTop) + "=" + TERMINATE_TOKENS
                    .getTokenId(currentOperator) + ")");
            return RULES[TERMINATE_TOKENS.getTokenId(opStackTop)][TERMINATE_TOKENS
                    .getTokenId(currentOperator)];
        } catch (Throwable e) {
            throw new RuntimeException("No rules were defined for some token!");
        }
    }
}

enum TOKENIZER {
    INSTANCE;

    private static final StringBuilder BUFFER = new StringBuilder();

    private static String clearExpression(String expression) {
        return expression.replaceAll(" ", "");
    }

    private static Character PREVIOUS_CHAR;

    private static void clean() {
        BUFFER.delete(0, BUFFER.length());
        PREVIOUS_CHAR = null;
    }

    private static boolean processNegativeNumbers(final String exp,
                                                  final int index) {
        char c = exp.charAt(index);
        if (('+' == c || '-' == c)
                && (PREVIOUS_CHAR == null || TERMINATE_TOKENS
                .getNegativeNumSensitiveToken().contains(PREVIOUS_CHAR))
                && !TERMINATE_TOKENS.isTerminateToken(exp.charAt(index + 1))) {
            BUFFER.append(c);
            return true;
        }
        return false;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List<?> exec(final String expression) {
        clean();
        String exp = clearExpression(expression);
        List result = new LinkedList();
        for (int i = 0; i < exp.length(); i++) {
            char c = exp.charAt(i);
            if (TERMINATE_TOKENS.isTerminateToken(c)) {

                if (processNegativeNumbers(exp, i))
                    continue;
                if (BUFFER.length() > 0) {
                    result.add(Double.valueOf(BUFFER.toString()));
                    BUFFER.delete(0, BUFFER.length());
                }
                result.add(c);
            } else {
                BUFFER.append(c);
            }
            PREVIOUS_CHAR = c;
        }
        return Collections.unmodifiableList(result);
    }

}

