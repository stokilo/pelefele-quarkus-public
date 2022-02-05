package com.sstec.qpelefele.aws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.SsmException;

import java.util.Optional;


public class AwsParameterStore {

    /**
     * Initialize AWS SSM client and fetch parameter name with provided name.
     * @param pParamName the parameter to fetch.
     * @return parameter value or empty string in case of error
     */
    public static Optional<JsonNode> getSSMParameterValue(String pParamName) {
        try(SsmClient ssmClient = SsmClient.builder().region(AwsConfig.REGION).build()) {

            GetParameterRequest parameterRequest = GetParameterRequest.builder()
                    .name(pParamName)
                    .build();

            GetParameterResponse parameterResponse = ssmClient.getParameter(parameterRequest);

            ObjectMapper objectMapper = new ObjectMapper();
            return Optional.of(objectMapper.readTree(parameterResponse.parameter().value()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
