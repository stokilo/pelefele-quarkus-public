package com.sstec.qpelefele.aws;

import software.amazon.awssdk.regions.Region;

public class AwsConfig {
    public static final Region REGION = Region.AP_SOUTH_1;

    public static final String SSM_PARAMETER_PREFIX = "/app-output/pelefele/";

    public static final String SECRET_MANAGER_PARAMETER_NAME = "all-stages/pelefele";
}
