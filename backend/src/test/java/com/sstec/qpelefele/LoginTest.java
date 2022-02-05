package com.sstec.qpelefele;

import com.sstec.qpelefele.config.OIDCRoleType;
import com.sstec.qpelefele.web.RestApiPathEnum;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.Claim;
import io.quarkus.test.security.oidc.OidcSecurity;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestProfile(TestProfiles.AuthTag.class)
@TestSecurity(user = TestConstants.REGULAR_USERNAME, roles = {OIDCRoleType.OIDCRole.REGULAR_USER})
@OidcSecurity(claims = {
        @Claim(key = "email", value = TestConstants.REGULAR_USER_EMAIL),
        @Claim(key = "sub", value = TestConstants.REGULAR_USER_OIDC_ID)
})
public class LoginTest {

    @Test
    public void whenLogin_return200() {
         given()
                .when().get(RestApiPathEnum.Constants.API_LOGIN)
                .then()
                .statusCode(200);
    }
}
