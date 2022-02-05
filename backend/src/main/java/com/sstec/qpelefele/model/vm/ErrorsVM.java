package com.sstec.qpelefele.model.vm;

import com.sstec.qpelefele.model.TypescriptSerializable;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.HashMap;
import java.util.Map;

@TypescriptSerializable
@RegisterForReflection
public class ErrorsVM {
    public Map<String, String> errors = new HashMap<>();

    public void addError(String fieldName, String error) {
        errors.put(fieldName, error);
    }
}
