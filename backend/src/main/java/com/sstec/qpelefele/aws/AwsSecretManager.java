package com.sstec.qpelefele.aws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import java.util.Optional;

public class AwsSecretManager {

    public static Optional<JsonNode> getSecretValue(String secretName) {
        try(SecretsManagerClient secretsClient = SecretsManagerClient.builder().region(AwsConfig.REGION).build()){
            GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();

            String valueResponse = secretsClient.getSecretValue(valueRequest).secretString();

            ObjectMapper objectMapper = new ObjectMapper();
            return Optional.of(objectMapper.readTree(valueResponse));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
