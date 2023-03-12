package com.kagg886.seiko.dic.model;

import com.kagg886.seiko.dic.exception.DictionaryOnLoadException;

import java.util.ArrayList;
import java.util.List;

public class DICParseResult {
    public boolean success = true;
    public List<Throwable> err = new ArrayList<>();
}
