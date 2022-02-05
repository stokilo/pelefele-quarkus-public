package com.sstec.qpelefele.config;

public enum OIDCRoleType {
    ADMIN(OIDCRole.ADMIN),
    REGULAR_USER(OIDCRole.REGULAR_USER);

    public static class OIDCRole {
        public static final String ADMIN = "ADMIN";
        public static final String REGULAR_USER = "REGULAR_USER";
    }

    OIDCRoleType(final String pRoleType) {
        if(!pRoleType.equals(this.name()))
            throw new IllegalArgumentException();
    }
}
