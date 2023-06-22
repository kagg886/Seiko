package com.kagg886.seiko.dic.entity.impl;

import com.kagg886.seiko.dic.entity.DictionaryCode;

import com.kagg886.seiko.dic.entity.DictionaryCode;
import com.kagg886.seiko.dic.session.AbsRuntime;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kagg886
 * @date 2023/5/26 16:00
 **/
public class ConditionalExpression extends Expression.If {

    private List<DictionaryCode> success;
    private List<DictionaryCode> failed;

    @Override
    public <T> boolean calc(AbsRuntime<T> runtime) {
        boolean result = super.calc(runtime);
        if (result) {
            if (success != null) {
                runtime.invoke(success,false);
            }
        } else {
            if (failed != null) {
                runtime.invoke(failed,false);
            }
        }
        return true;
    }

    public ConditionalExpression(int line, String code) {
        super(line, code);
    }

    public List<DictionaryCode> getSuccess() {
        return success;
    }

    public void setSuccess(List<DictionaryCode> success) {
        this.success = success;
    }

    public List<DictionaryCode> getFailed() {
        return failed;
    }

    public void setFailed(List<DictionaryCode> failed) {
        this.failed = failed;
    }
}
