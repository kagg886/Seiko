package com.kagg886.seiko.dic;

import com.kagg886.seiko.dic.entity.DictionaryCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kagg886
 * @date 2023/5/26 16:00
 **/
public class ConditionalExpression extends DictionaryCode {

    private List<DictionaryCode> success;
    private List<DictionaryCode> failed;

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
