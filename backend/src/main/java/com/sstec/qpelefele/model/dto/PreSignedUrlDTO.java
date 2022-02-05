package com.sstec.qpelefele.model.dto;


import com.sstec.qpelefele.model.TypescriptSerializable;
import io.quarkus.runtime.annotations.RegisterForReflection;

@TypescriptSerializable
@RegisterForReflection
public class PreSignedUrlDTO {
    public String url;
    public String fileName;

    public PreSignedUrlDTO(String url, String fileName) {
        this.url = url;
        this.fileName = fileName;
    }
}
