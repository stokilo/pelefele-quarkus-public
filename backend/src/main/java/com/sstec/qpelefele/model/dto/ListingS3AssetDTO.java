package com.sstec.qpelefele.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sstec.qpelefele.model.TypescriptSerializable;
import io.quarkus.runtime.annotations.RegisterForReflection;


@TypescriptSerializable
@RegisterForReflection
public class ListingS3AssetDTO {

    public ListingS3AssetDTO() {
    }

    public String fileName;
    public Boolean isCover;

    @JsonIgnore
    public ListingDTO listing;
}
