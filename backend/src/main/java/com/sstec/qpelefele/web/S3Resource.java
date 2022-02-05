package com.sstec.qpelefele.web;

import com.sstec.qpelefele.aws.AwsConfig;
import com.sstec.qpelefele.config.OIDCRoleType;
import com.sstec.qpelefele.model.dto.PreSignedUrlDTO;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.jboss.logging.Logger;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/api")
public class S3Resource {

    private static final int MAX_S3_BATCH_SIZE = 10;

    private static final Logger log = Logger.getLogger(S3Resource.class);

    @Inject
    @Claim(standard = Claims.sub)
    String subject;

    @ConfigProperty(name = "aws.iac.ssm.s3.bucket.uploadBucketName")
    String localAssetsBucketName;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(RestApiPathEnum.Constants.S3_GENERATE_SIGNED_URL)
    @RolesAllowed({OIDCRoleType.OIDCRole.REGULAR_USER})
    public Response generatePreSignUrl(@QueryParam("count") int count) {

        if (count > S3Resource.MAX_S3_BATCH_SIZE) {
            throw new BadRequestException(String.format("Reached limit of number of S3 urls, max is: %s, received: %s",
                    S3Resource.MAX_S3_BATCH_SIZE, count));
        }

        List<PreSignedUrlDTO> preSignedUrlDTOS = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < count; i++) {
            String fileName = String.format("%s/%s/%s/%s/%s-%s", today.getYear(), today.getMonthValue(), today.getDayOfMonth(),
                    subject, UUID.randomUUID().toString(), count);
            S3Presigner preSigner = S3Presigner.builder().region(AwsConfig.REGION).build();
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(localAssetsBucketName)
                    .key(fileName)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .putObjectRequest(objectRequest)
                    .build();
            PresignedPutObjectRequest preSignedRequest = preSigner.presignPutObject(presignRequest);
            preSignedUrlDTOS.add(new PreSignedUrlDTO(preSignedRequest.url().toString(), fileName));
        }

        return Response.ok(preSignedUrlDTOS).build();
    }

}
