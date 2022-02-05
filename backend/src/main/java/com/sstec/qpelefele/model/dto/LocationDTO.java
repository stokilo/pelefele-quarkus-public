package com.sstec.qpelefele.model.dto;


import com.sstec.qpelefele.model.TypescriptSerializable;
import io.quarkus.runtime.annotations.RegisterForReflection;

@TypescriptSerializable
@RegisterForReflection
public class LocationDTO {
    public String id;
    public String location;
}
