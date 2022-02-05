package com.sstec.qpelefele.model;

public enum ListingPropertyType {
    APARTMENT("APARTMENT"),
    HOUSE("HOUSE");

    private String code;

    private ListingPropertyType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
