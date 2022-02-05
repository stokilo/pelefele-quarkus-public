package com.sstec.qpelefele.model.vm;

import com.sstec.qpelefele.model.ListingPropertyType;
import com.sstec.qpelefele.model.ListingPurpose;
import com.sstec.qpelefele.model.TypescriptSerializable;
import com.sstec.qpelefele.validation.ValueOfEnum;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.QueryParam;

@TypescriptSerializable
@RegisterForReflection
public class ListingSearchParamsVM {
    @QueryParam("locationId")
    @Min(value = 0)
    @Max(value = Integer.MAX_VALUE)
    @Digits(integer = 10, fraction = 0)
    public String locationId;

    @QueryParam("propertyType")
    @ValueOfEnum(enumClass = ListingPropertyType.class)
    public String propertyType;

    @QueryParam("purpose")
    @ValueOfEnum(enumClass = ListingPurpose.class)
    public String purpose;

    @QueryParam("minPrice")
    @Min(value = 0)
    @Max(value = Integer.MAX_VALUE - 1)
    @Digits(integer = 10, fraction = 0)
    public String minPrice;

    @QueryParam("maxPrice")
    @Min(value = 1)
    @Max(value = Integer.MAX_VALUE)
    @Digits(integer = 10, fraction = 0)
    public String maxPrice;

    @QueryParam("minArea")
    @Min(value = 0)
    @Max(value = Integer.MAX_VALUE - 1)
    @Digits(integer = 10, fraction = 0)
    public String minArea;

    @QueryParam("maxArea")
    @Min(value = 1)
    @Max(value = Integer.MAX_VALUE)
    @Digits(integer = 10, fraction = 0)
    public String maxArea;

    @QueryParam("sort")
    public String sort;

    @QueryParam("pageNumber")
    @Min(value = 0)
    @Max(value = 100000)
    @Digits(integer = 10, fraction = 0)
    public String pageNumber;

    @QueryParam("pageSize")
    @Min(value = 1)
    @Max(value = 100)
    @Digits(integer = 10, fraction = 0)
    public String pageSize;
}
