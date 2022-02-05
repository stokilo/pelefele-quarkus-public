package com.sstec.qpelefele;

import com.sstec.qpelefele.model.*;
import com.sstec.qpelefele.model.vm.ListingSearchParamsVM;
import com.sstec.qpelefele.model.vm.ListingSearchVM;
import com.sstec.qpelefele.web.RestApiPathEnum;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wildfly.common.Assert;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestProfile(TestProfiles.ListingTag.class)
public class ListingSearchTest {

    ListingSearchParamsVM listingSearchParamsVM;

    static Location testLocation;

    @BeforeAll
    @Transactional
    public static void init(){
        testLocation = Location.findById(100L);
        Listing.delete("location", testLocation);

        Listing apartmentSale1 = createListing(ListingPropertyType.APARTMENT, ListingPurpose.SALE, new BigDecimal(1000), new BigDecimal(100));
        Listing apartmentSale2 = createListing(ListingPropertyType.APARTMENT, ListingPurpose.SALE, new BigDecimal(2000), new BigDecimal(200));

        Listing houseRental1 = createListing(ListingPropertyType.HOUSE, ListingPurpose.RENTAL, new BigDecimal(1000), new BigDecimal(100));
        Listing houseRental2 = createListing(ListingPropertyType.HOUSE, ListingPurpose.RENTAL, new BigDecimal(2000), new BigDecimal(200));

        apartmentSale1.persist();
        apartmentSale2.persist();
        houseRental1.persist();
        houseRental2.persist();
    }

    private static Listing createListing(ListingPropertyType listingPropertyType, ListingPurpose purpose,
                                         BigDecimal price, BigDecimal area){
        Listing listing = new Listing();
        listing.propertyType = listingPropertyType;
        listing.price = price;
        listing.area = area;
        listing.purpose = purpose;
        listing.location = testLocation;
        // regular test user
        listing.user = OIDCUser.findByUUID("1111-2222-3333-4444");
        listing.title = "";
        return listing;
    }

    @BeforeEach
    public void setup() {
        listingSearchParamsVM = new ListingSearchParamsVM();
        listingSearchParamsVM.minPrice = "0";
        listingSearchParamsVM.maxPrice = "100";
        listingSearchParamsVM.minArea = "0";
        listingSearchParamsVM.maxArea = "100";

        listingSearchParamsVM.propertyType = ListingPropertyType.APARTMENT.getCode();
        listingSearchParamsVM.purpose = ListingPurpose.RENTAL.getCode();

        listingSearchParamsVM.locationId = testLocation.id.toString();
        listingSearchParamsVM.sort = "";
        listingSearchParamsVM.pageSize = "10";
        listingSearchParamsVM.pageNumber = "0";
    }

    private Map<String, ?> toMap(ListingSearchParamsVM params) {
        Map<String, String> map = new HashMap<>();
        map.put("minPrice", params.minPrice);
        map.put("maxPrice", params.maxPrice);
        map.put("minArea", params.minArea);
        map.put("maxArea", params.maxArea);

        map.put("propertyType", params.propertyType);
        map.put("purpose", params.purpose);

        map.put("locationId", params.locationId);
        map.put("sort", params.sort);
        map.put("pageNumber", params.pageNumber.toString());
        map.put("pageSize", params.pageSize.toString());
        return map;
    }

    @Test
    public void whenSearchValidateParams_searchOk() {
        assertSearchStatus(200);
    }

    @Test
    public void whenSearchValidateParams_minPrice() {
        listingSearchParamsVM.minPrice = "";
        assertSearchStatus(200);
        listingSearchParamsVM.minPrice = "null";
        assertSearchStatus(400);

        listingSearchParamsVM.minPrice = "-1";
        assertSearchStatus(400);
        listingSearchParamsVM.minPrice = "0.01";
        assertSearchStatus(400);
        listingSearchParamsVM.minPrice = "0,01";
        assertSearchStatus(400);
        listingSearchParamsVM.minPrice = "00";
        assertSearchStatus(200);

        listingSearchParamsVM.minPrice = String.valueOf(Double.MAX_VALUE);
        assertSearchStatus(400);
        listingSearchParamsVM.minPrice = String.valueOf(Integer.MIN_VALUE);
        assertSearchStatus(400);

        // minvalue is a [max int - 1] to allow select [max int - 1, max int] range for a price
        listingSearchParamsVM.minPrice = String.valueOf(Integer.MAX_VALUE);
        assertSearchStatus(400);
        listingSearchParamsVM.minPrice = String.valueOf(Integer.MAX_VALUE-1);
        assertSearchStatus(200);

        listingSearchParamsVM.minPrice = String.valueOf(1900);
        assertSearchStatus(200);
    }


    @Test
    public void whenSearchValidateParams_maxPrice() {
        listingSearchParamsVM.maxPrice = "";
        assertSearchStatus(200);
        listingSearchParamsVM.maxPrice = "null";
        assertSearchStatus(400);

        listingSearchParamsVM.maxPrice = "-1";
        assertSearchStatus(400);
        listingSearchParamsVM.maxPrice = "0.01";
        assertSearchStatus(400);
        listingSearchParamsVM.maxPrice = "0,01";
        assertSearchStatus(400);
        listingSearchParamsVM.maxPrice = "00";
        assertSearchStatus(400);

        listingSearchParamsVM.maxPrice = String.valueOf(Double.MAX_VALUE);
        assertSearchStatus(400);
        listingSearchParamsVM.maxPrice = String.valueOf(Integer.MIN_VALUE);
        assertSearchStatus(400);

        listingSearchParamsVM.maxPrice = String.valueOf(Integer.MAX_VALUE);
        assertSearchStatus(200);

        listingSearchParamsVM.maxPrice = "0";
        assertSearchStatus(400);
        listingSearchParamsVM.maxPrice = "1";
        assertSearchStatus(200);
    }

    @Test
    public void whenSearchValidateParams_minArea() {
        listingSearchParamsVM.minArea = "";
        assertSearchStatus(200);
        listingSearchParamsVM.minArea = "null";
        assertSearchStatus(400);

        listingSearchParamsVM.minArea = "-1";
        assertSearchStatus(400);
        listingSearchParamsVM.minArea = "0.01";
        assertSearchStatus(400);
        listingSearchParamsVM.minArea = "0,01";
        assertSearchStatus(400);
        listingSearchParamsVM.minArea = "00";
        assertSearchStatus(200);

        listingSearchParamsVM.minArea = String.valueOf(Double.MAX_VALUE);
        assertSearchStatus(400);
        listingSearchParamsVM.minArea = String.valueOf(Integer.MIN_VALUE);
        assertSearchStatus(400);

        // minvalue is a [max int - 1] to allow select [max int - 1, max int] range for a price
        listingSearchParamsVM.minArea = String.valueOf(Integer.MAX_VALUE);
        assertSearchStatus(400);
        listingSearchParamsVM.minArea = String.valueOf(Integer.MAX_VALUE-1);
        assertSearchStatus(200);

        listingSearchParamsVM.minArea = String.valueOf(1900);
        assertSearchStatus(200);
    }


    @Test
    public void whenSearchValidateParams_maxArea() {
        listingSearchParamsVM.maxArea = "";
        assertSearchStatus(200);
        listingSearchParamsVM.maxArea = "null";
        assertSearchStatus(400);

        listingSearchParamsVM.maxArea = "-1";
        assertSearchStatus(400);
        listingSearchParamsVM.maxArea = "0.01";
        assertSearchStatus(400);
        listingSearchParamsVM.maxArea = "0,01";
        assertSearchStatus(400);
        listingSearchParamsVM.maxArea = "00";
        assertSearchStatus(400);

        listingSearchParamsVM.maxArea = String.valueOf(Double.MAX_VALUE);
        assertSearchStatus(400);
        listingSearchParamsVM.maxArea = String.valueOf(Integer.MIN_VALUE);
        assertSearchStatus(400);

        listingSearchParamsVM.maxArea = String.valueOf(Integer.MAX_VALUE);
        assertSearchStatus(200);

        listingSearchParamsVM.maxArea = "0";
        assertSearchStatus(400);
        listingSearchParamsVM.maxArea = "1";
        assertSearchStatus(200);
    }

    @Test
    public void whenSearchValidateParams_propertyType() {
        listingSearchParamsVM.propertyType = "";
        assertSearchStatus(400);
        listingSearchParamsVM.propertyType = "null";
        assertSearchStatus(400);
        listingSearchParamsVM.propertyType = "0";
        assertSearchStatus(400);
        listingSearchParamsVM.propertyType = "1";
        assertSearchStatus(400);
        listingSearchParamsVM.propertyType = ListingPropertyType.HOUSE.getCode();
        assertSearchStatus(200);
        listingSearchParamsVM.propertyType = ListingPropertyType.APARTMENT.getCode();
        assertSearchStatus(200);
    }

    @Test
    public void whenSearchValidateParams_purpose() {
        listingSearchParamsVM.purpose = "";
        assertSearchStatus(400);
        listingSearchParamsVM.purpose = "null";
        assertSearchStatus(400);
        listingSearchParamsVM.purpose = "0";
        assertSearchStatus(400);
        listingSearchParamsVM.purpose = "1";
        assertSearchStatus(400);
        listingSearchParamsVM.purpose = ListingPurpose.SALE.getCode();
        assertSearchStatus(200);
        listingSearchParamsVM.purpose = ListingPurpose.RENTAL.getCode();
        assertSearchStatus(200);
    }

    @Test
    public void whenSearchValidateParams_locationId() {
        listingSearchParamsVM.locationId = "";
        assertSearchStatus(400);
        listingSearchParamsVM.locationId = "null";
        assertSearchStatus(400);

        listingSearchParamsVM.locationId = "-1";
        assertSearchStatus(400);
        listingSearchParamsVM.locationId = "0.01";
        assertSearchStatus(400);
        listingSearchParamsVM.locationId = "0,01";
        assertSearchStatus(400);
        listingSearchParamsVM.locationId = "00";
        assertSearchStatus(200);

        listingSearchParamsVM.locationId = String.valueOf(Double.MAX_VALUE);
        assertSearchStatus(400);
        listingSearchParamsVM.locationId = String.valueOf(Integer.MIN_VALUE);
        assertSearchStatus(400);

        listingSearchParamsVM.locationId = String.valueOf(Integer.MAX_VALUE);
        assertSearchStatus(200);

        listingSearchParamsVM.locationId = String.valueOf(100);
        assertSearchStatus(200);
    }

    @Test
    public void whenSearchValidateParams_pageNumber() {
        listingSearchParamsVM.pageNumber = "";
        assertSearchStatus(400);
        listingSearchParamsVM.pageNumber = "null";
        assertSearchStatus(400);

        listingSearchParamsVM.pageNumber = "-1";
        assertSearchStatus(400);
        listingSearchParamsVM.pageNumber = "0.01";
        assertSearchStatus(400);
        listingSearchParamsVM.pageNumber = "0,01";
        assertSearchStatus(400);
        listingSearchParamsVM.pageNumber = "00";
        assertSearchStatus(200);

        listingSearchParamsVM.pageNumber = String.valueOf(Double.MAX_VALUE);
        assertSearchStatus(400);
        listingSearchParamsVM.pageNumber = String.valueOf(Integer.MIN_VALUE);
        assertSearchStatus(400);

        listingSearchParamsVM.pageNumber = "100000";
        assertSearchStatus(200);
        listingSearchParamsVM.pageNumber = "100001";
        assertSearchStatus(400);

        listingSearchParamsVM.pageNumber = String.valueOf(100);
        assertSearchStatus(200);
    }

    @Test
    public void whenSearchValidateParams_pageSize() {
        listingSearchParamsVM.pageSize = "";
        assertSearchStatus(400);
        listingSearchParamsVM.pageSize = "null";
        assertSearchStatus(400);

        listingSearchParamsVM.pageSize = "-1";
        assertSearchStatus(400);
        listingSearchParamsVM.pageSize = "0.01";
        assertSearchStatus(400);
        listingSearchParamsVM.pageSize = "0,01";
        assertSearchStatus(400);
        listingSearchParamsVM.pageSize = "00";
        assertSearchStatus(400);

        listingSearchParamsVM.pageSize = String.valueOf(Double.MAX_VALUE);
        assertSearchStatus(400);
        listingSearchParamsVM.pageSize = String.valueOf(Integer.MIN_VALUE);
        assertSearchStatus(400);

        listingSearchParamsVM.pageSize = String.valueOf(Integer.MAX_VALUE);
        assertSearchStatus(400);

        listingSearchParamsVM.pageSize = "100";
        assertSearchStatus(200);
        listingSearchParamsVM.pageSize = "101";
        assertSearchStatus(400);

        listingSearchParamsVM.pageSize = "0";
        assertSearchStatus(400);
        listingSearchParamsVM.pageSize = "1";
        assertSearchStatus(200);
    }

    @Test
    public void whenSearchValidateParams_sort() {
        // todo: define sort format
        listingSearchParamsVM.sort = "";
        assertSearchStatus(200);
    }

    @Test
    public void whenSearchFor_apartmentForSale() {
        listingSearchParamsVM.purpose = ListingPurpose.SALE.getCode();
        listingSearchParamsVM.minArea = "0";
        listingSearchParamsVM.maxArea = "500";
        listingSearchParamsVM.minPrice = "0";
        listingSearchParamsVM.maxPrice = "999";
        searchAndExpectCount(0);
        listingSearchParamsVM.maxPrice = "999";
        searchAndExpectCount(0);
        listingSearchParamsVM.maxPrice = "1000";
        searchAndExpectCount(1);
        listingSearchParamsVM.maxPrice = "1999";
        searchAndExpectCount(1);
        listingSearchParamsVM.maxPrice = "2000";
        searchAndExpectCount(2);
        listingSearchParamsVM.minArea = "101";
        listingSearchParamsVM.maxArea = "102";
        searchAndExpectCount(0);
        listingSearchParamsVM.maxArea = "200";
        searchAndExpectCount(1);
        listingSearchParamsVM.minArea = "100";
        searchAndExpectCount(2);
        listingSearchParamsVM.locationId = "101";
        searchAndExpectCount(0);
        listingSearchParamsVM.locationId = testLocation.id.toString();
        searchAndExpectCount(2);
    }

    @Test
    public void whenSearchFor_houseForRental() {
        listingSearchParamsVM.propertyType = ListingPropertyType.HOUSE.getCode();
        listingSearchParamsVM.purpose = ListingPurpose.RENTAL.getCode();
        listingSearchParamsVM.minArea = "0";
        listingSearchParamsVM.maxArea = "500";
        listingSearchParamsVM.minPrice = "0";
        listingSearchParamsVM.maxPrice = "999";
        searchAndExpectCount(0);
        listingSearchParamsVM.maxPrice = "999";
        searchAndExpectCount(0);
        listingSearchParamsVM.maxPrice = "1000";
        searchAndExpectCount(1);
        listingSearchParamsVM.maxPrice = "1999";
        searchAndExpectCount(1);
        listingSearchParamsVM.maxPrice = "2000";
        searchAndExpectCount(2);
        listingSearchParamsVM.minArea = "101";
        listingSearchParamsVM.maxArea = "102";
        searchAndExpectCount(0);
        listingSearchParamsVM.maxArea = "200";
        searchAndExpectCount(1);
        listingSearchParamsVM.minArea = "100";
        searchAndExpectCount(2);
        listingSearchParamsVM.locationId = "101";
        searchAndExpectCount(0);
        listingSearchParamsVM.locationId = testLocation.id.toString();
        searchAndExpectCount(2);
    }

    @Test
    public void whenSearchWithoutAllFilters_useDefaults() {
        listingSearchParamsVM.propertyType = ListingPropertyType.HOUSE.getCode();
        listingSearchParamsVM.purpose = ListingPurpose.RENTAL.getCode();
        listingSearchParamsVM.minArea = "";
        listingSearchParamsVM.maxArea = "";
        listingSearchParamsVM.minPrice = "0";
        listingSearchParamsVM.maxPrice = "999999";
        searchAndExpectCount(2);
        listingSearchParamsVM.minArea = "0";
        listingSearchParamsVM.maxArea = "9999999";
        listingSearchParamsVM.minPrice = "";
        listingSearchParamsVM.maxPrice = "";
        searchAndExpectCount(2);
    }

    private void assertSearchStatus(Integer statusCode) {
        given()
                .params(toMap(listingSearchParamsVM))
                .when().get(RestApiPathEnum.Constants.API_LISTING_SEARCH)
                .then()
                .statusCode(statusCode);
    }

    private void searchAndExpectCount(int expectedCount){
        ListingSearchVM listingSearchVM = given()
                .params(toMap(listingSearchParamsVM))
                .when().get(RestApiPathEnum.Constants.API_LISTING_SEARCH)
                .then()
                .statusCode(200).extract().as(ListingSearchVM.class);
        Assertions.assertEquals(listingSearchVM.listings.size(), expectedCount);
    }



}
