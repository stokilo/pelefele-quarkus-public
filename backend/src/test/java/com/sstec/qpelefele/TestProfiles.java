package com.sstec.qpelefele;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.*;

public class TestProfiles {

    /**
     * Tag for features under development. Convenient to only run subset of tests.
     */
    public static class DevTag implements QuarkusTestProfile {
        @Override
        public Set<String> tags() {
            return Collections.singleton("dev");
        }
    }

    /**
     * Tag all tests for auth layer, all features.
     */
    public static class AuthTag implements QuarkusTestProfile {
        @Override
        public Set<String> tags() {
            return Collections.singleton("auth");
        }
    }

    /**
     * Tag all tests for listing feature.
     */
    public static class ListingTag implements QuarkusTestProfile {
        @Override
        public Set<String> tags() {
            return new HashSet<>(List.of("listing"));
        }
    }

}