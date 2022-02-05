package com.sstec.qpelefele.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.sstec.qpelefele.aws.AwsConfig;
import com.sstec.qpelefele.aws.AwsSecretManager;
import io.quarkus.arc.Unremovable;
import io.quarkus.credentials.CredentialsProvider;
import io.quarkus.runtime.configuration.ProfileManager;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
@Unremovable
public class MyCredentialsProvider implements CredentialsProvider {

    @Inject
    Logger log;

    @Override
    public Map<String, String> getCredentials(String credentialsProviderName) {
        Map<String, String> properties = new HashMap<>();
        try {
            // todo: ehh, aws cdk hardcodes username password but secret is shared between stages, how to solve this?
            // todo: try to replace it with AppConfigSource
            if (ProfileManager.getActiveProfile().equalsIgnoreCase("stage") ||
                ProfileManager.getActiveProfile().equalsIgnoreCase("prod")) {
                log.info("Fetch database credentials from AWS secret");
                JsonNode secret = AwsSecretManager.getSecretValue(AwsConfig.SECRET_MANAGER_PARAMETER_NAME).orElseThrow();
                properties.put(USER_PROPERTY_NAME, secret.get("username").asText());
                properties.put(PASSWORD_PROPERTY_NAME, secret.get("password").asText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }
}
