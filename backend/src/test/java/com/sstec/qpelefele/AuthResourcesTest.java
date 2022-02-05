package com.sstec.qpelefele;

import com.sstec.qpelefele.config.OIDCRoleType;
import com.sstec.qpelefele.web.RestApiPathEnum;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;

@QuarkusTest
@TestProfile(TestProfiles.AuthTag.class)
public class AuthResourcesTest {

    @Test
    public void whenAnnotatedWithAuthenticated_tokenIsRequired() {
        given().when().get(RestApiPathEnum.Constants.API_LISTINGS).then().statusCode(401);
        given().when().header("Content-Type", "application/json").post(RestApiPathEnum.Constants.API_LISTINGS).then().statusCode(401);
        given().when().get(RestApiPathEnum.Constants.API_LOGIN).then().statusCode(401);
    }

    @Test
    @TestSecurity(user = TestConstants.REGULAR_USERNAME, roles = {OIDCRoleType.OIDCRole.REGULAR_USER})
    public void whenAnnotatedWithAdminRole_noOtherRoleAllowed() {
    }

    @Test
    @TestSecurity(user = TestConstants.REGULAR_USERNAME,roles = {OIDCRoleType.OIDCRole.ADMIN})
    public void whenAnnotatedWithRegularUserRole_noOtherRoleAllowed() {
        given().when().header("Content-Type", "application/json").post(RestApiPathEnum.Constants.API_LISTINGS).then().statusCode(403);
    }
}
