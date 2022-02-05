package com.sstec.qpelefele;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sstec.qpelefele.config.OIDCRoleType;
import com.sstec.qpelefele.model.ListingPropertyType;
import com.sstec.qpelefele.model.ListingPurpose;
import com.sstec.qpelefele.model.dto.ListingDTO;
import com.sstec.qpelefele.model.dto.ListingS3AssetDTO;
import com.sstec.qpelefele.model.dto.LocationDTO;
import com.sstec.qpelefele.model.vm.ListingSearchVM;
import com.sstec.qpelefele.web.RestApiPathEnum;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.Claim;
import io.quarkus.test.security.oidc.OidcSecurity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@TestProfile(TestProfiles.ListingTag.class)
@TestSecurity(user = TestConstants.REGULAR_USERNAME, roles = {OIDCRoleType.OIDCRole.REGULAR_USER})
@OidcSecurity(claims = {
        @Claim(key = "email", value = TestConstants.REGULAR_USER_EMAIL),
        @Claim(key = "sub", value = TestConstants.REGULAR_USER_OIDC_ID)
})
public class ListingResourceTest {

    @Test
    public void whenGetListingWithoutParams_return400() {
        given()
                .when().get(RestApiPathEnum.Constants.API_LISTINGS)
                .then()
                .statusCode(400);
    }

    @Test
    public void whenGetListing_return200() {
         given()
                .param("pageNumber", 0)
                .param("pageSize", 10)
                .when().get(RestApiPathEnum.Constants.API_LISTINGS)
                .then()
                .statusCode(200)
                .extract().as(ListingSearchVM.class);
    }

    @Test
    public void whenPostListing_return201() throws JsonProcessingException {
        ListingDTO listingDTO = new ListingDTO();
        listingDTO.price = new BigDecimal(110);
        listingDTO.title = "Test title";
        listingDTO.propertyType = ListingPropertyType.APARTMENT;
        listingDTO.purpose = ListingPurpose.RENTAL;
        listingDTO.area = new BigDecimal(10L);
        listingDTO.location = new LocationDTO();
        listingDTO.location.id = "1";
        listingDTO.location.location = "Aroniowa";
        ListingS3AssetDTO listingS3AssetDTO1 = new ListingS3AssetDTO();
        listingS3AssetDTO1.fileName = "cover.png";
        listingS3AssetDTO1.isCover = true;
        ListingS3AssetDTO listingS3AssetDTO2 = new ListingS3AssetDTO();
        listingS3AssetDTO2.fileName = "pic1.png";
        listingS3AssetDTO2.isCover = false;
        listingDTO.assets.add(listingS3AssetDTO1);
        listingDTO.assets.add(listingS3AssetDTO2);
        ObjectMapper objectMapper = new ObjectMapper();
        given()
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(listingDTO))
                .when().post(RestApiPathEnum.Constants.API_LISTINGS)
                .then()
                .statusCode(201)
                .body(is(""));
    }

}
