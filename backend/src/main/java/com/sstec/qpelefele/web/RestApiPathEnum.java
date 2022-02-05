package com.sstec.qpelefele.web;

public enum RestApiPathEnum {

    LISTINGS(Constants.LISTINGS),
    API_LISTINGS(Constants.API_LISTINGS),

    LOGIN(Constants.LOGIN),
    API_LOGIN(Constants.API_LOGIN),

    LOCATION_SEARCH(Constants.LOCATION_SEARCH),
    API_LOCATION_SEARCH(Constants.API_LOCATION_SEARCH),

    LISTING_SEARCH(Constants.LISTING_SEARCH),
    API_LISTING_SEARCH(Constants.API_LISTING_SEARCH);

    public static class Constants {
        public static final String API_NAME = "/api";

        public static final String LOGIN = "/login";
        public static final String API_LOGIN = String.format("%s%s",  Constants.API_NAME, Constants.LOGIN);

        public static final String LISTINGS = "/listings";
        public static final String API_LISTINGS = String.format("%s%s",  Constants.API_NAME, Constants.LISTINGS);

        public static final String S3_GENERATE_SIGNED_URL = "/s3/generate-signed-url";
        public static final String API_S3_GENERATE_SIGNED_URL = String.format("%s%s",
                Constants.API_NAME, Constants.S3_GENERATE_SIGNED_URL);

        public static final String LOCATION_SEARCH = "/search/location";
        public static final String API_LOCATION_SEARCH = String.format("%s%s",  Constants.API_NAME, Constants.LOCATION_SEARCH);

        public static final String LISTING_SEARCH = "/search/listing";
        public static final String API_LISTING_SEARCH = String.format("%s%s",  Constants.API_NAME, Constants.LISTING_SEARCH);
    }

    RestApiPathEnum(final String path) {
    }
}
