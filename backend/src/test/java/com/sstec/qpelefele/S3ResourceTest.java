package com.sstec.qpelefele;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sstec.qpelefele.config.OIDCRoleType;
import com.sstec.qpelefele.web.RestApiPathEnum;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.Claim;
import io.quarkus.test.security.oidc.OidcSecurity;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
@TestProfile(TestProfiles.ListingTag.class)
@TestSecurity(user = TestConstants.REGULAR_USERNAME, roles = {OIDCRoleType.OIDCRole.REGULAR_USER})
@OidcSecurity(claims = {
        @Claim(key = "email", value = TestConstants.REGULAR_USER_EMAIL),
        @Claim(key = "sub", value = TestConstants.REGULAR_USER_OIDC_ID)
})
public class S3ResourceTest {

    @Test
    public void whenGetS3Url_return201WithListOfUrls() throws JsonProcessingException {
        given()
                .log().all()
                .header("Content-Type", "application/json")
                .param("count", 1)
                .when().get(RestApiPathEnum.Constants.API_S3_GENERATE_SIGNED_URL)
                .then()
                .statusCode(200)
                .body(containsString("https://test.s3.ap-south-1.amazonaws.com/"));
    }

}
