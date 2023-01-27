package com.kagg886.seiko.dic.entity.impl;

import com.kagg886.seiko.dic.DictionaryUtil;
import com.kagg886.seiko.dic.entity.DictionaryCode;
import com.kagg886.seiko.dic.session.AbsRuntime;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.impl
 * @className: Expression
 * @author: kagg886
 * @description: 代表词库的表达式
 * @date: 2023/1/16 18:39
 * @version: 1.0
 */
public abstract class Expression extends DictionaryCode {

    public Expression(int line, String code) {
        super(line, code);
    }


    public static class If extends Expression {
        private final String express;

        public If(int line, String code) {
            super(line, code);
            this.express = code.substring(3);
        }

        public <T> boolean calc(AbsRuntime<T> runtime) {
            System.out.println("清理变量前的P表达式:" + express);
            String p = DictionaryUtil.cleanVariableCode(express, runtime);
            System.out.println("清理变量后的P表达式:" + p);
            return DictionaryUtil.compareAsString(p);
        }
    }

    public static class Else extends Expression {

        public Else(int line, String code) {
            super(line, code);
        }
    }

    public static class Return extends Expression {

        public Return(int line, String code) {
            super(line, code);
        }
    }
}
