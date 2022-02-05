package com.sstec.qpelefele.model;

public enum ListingPurpose {
    SALE("SALE"),
    RENTAL("RENTAL");

    private String code;

    private ListingPurpose(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
