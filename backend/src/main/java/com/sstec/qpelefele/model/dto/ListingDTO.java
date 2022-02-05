package com.sstec.qpelefele.model.dto;

import com.sstec.qpelefele.model.*;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.persistence.Column;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@TypescriptSerializable
@RegisterForReflection
public class ListingDTO {

    public ListingDTO() {
    }

    public Long id;

    @NotBlank(message="{listing.title.required}")
    @Size(min = 5, max = 255, message = "{listing.title.size}")
    public String title;

    @Min(value = 100, message = "{listing.min-price}")
    public BigDecimal price;

    @NotNull
    public ListingPropertyType propertyType;

    @NotNull
    public ListingPurpose purpose;

    @Min(value = 1)
    @Max(value = 99999999999L)
    public BigDecimal area;

    public LocationDTO location;

    public List<ListingS3AssetDTO> assets = new ArrayList<>();

}
