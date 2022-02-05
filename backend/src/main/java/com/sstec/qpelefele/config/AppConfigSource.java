package com.sstec.qpelefele.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.sstec.qpelefele.aws.AwsConfig;
import com.sstec.qpelefele.aws.AwsParameterStore;
import io.quarkus.arc.Unremovable;
import io.quarkus.runtime.configuration.ProfileManager;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
@Unremovable
public class AppConfigSource implements ConfigSource {

    private static final Map<String, String> configuration = new HashMap<>();

    private static final Logger log = Logger.getLogger(AppConfigSource.class);

    static {
        try {
            log.info("Active profile [" + ProfileManager.getActiveProfile() + "]");

            // todo: remove check for 'test' profile and cache configuration?
            if (ProfileManager.getActiveProfile().equalsIgnoreCase("test")){
                // todo: this is for testing only, leave it for now
                configuration.put("quarkus.oidc.auth-server-url", "https://cognito-idp.ap-south-1.amazonaws.com/ap-south-1_V2aLxI8kN");
                configuration.put("aws.iac.cognitoAdminGroupName", "admin-group");
                configuration.put("aws.iac.cognitoRegularUserGroupName", "regular-users-group");
                configuration.put("aws.iac.ssm.s3.bucket.uploadBucketName", "test");
            } else {
                log.info("Fetch SSM parameters to configure Cognito");
                String ssmParameterName = AwsConfig.SSM_PARAMETER_PREFIX + ProfileManager.getActiveProfile();
                log.info("Fetch from parameter name: " + ssmParameterName);
                JsonNode ssmParamJsonNode = AwsParameterStore.getSSMParameterValue(ssmParameterName).orElseThrow();
                log.info("Fetch 1");
                String cognitoUserPoolId = ssmParamJsonNode.get("cognitoUserPoolId").asText();
                String quarkusOIDCServerUrl = String.format("https://cognito-idp.%s.amazonaws.com/%s",
                        AwsConfig.REGION.id(), cognitoUserPoolId);
                // To test if that works, append  /.well-known/openid-configuration to the url:
                // https://cognito-idp.ap-south-1.amazonaws.com/ap-south-1_jRMxU35MW/.well-known/openid-configuration
                configuration.put("quarkus.oidc.auth-server-url", quarkusOIDCServerUrl);

                String cognitoAdminGroupName = ssmParamJsonNode.get("cognitoAdminGroupName").asText();
                configuration.put("aws.iac.cognitoAdminGroupName", cognitoAdminGroupName);

                String cognitoRegularUserGroupName = ssmParamJsonNode.get("cognitoRegularUserGroupName").asText();
                configuration.put("aws.iac.cognitoRegularUserGroupName", cognitoRegularUserGroupName);

                String uploadBucketName = ssmParamJsonNode.get("uploadBucketName").asText();
                configuration.put("aws.iac.ssm.s3.bucket.uploadBucketName", uploadBucketName);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Set<String> getPropertyNames() {
        return configuration.keySet();
    }

    @Override
    public String getValue(final String propertyName) {
        return configuration.get(propertyName);
    }

    @Override
    public String getName() {
        return AppConfigSource.class.getSimpleName();
    }
}
