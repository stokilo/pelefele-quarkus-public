package com.sstec.qpelefele.web;

import com.sstec.qpelefele.model.vm.ListingSearchParamsVM;
import com.sstec.qpelefele.service.ListingService;
import org.mapstruct.ap.internal.util.Strings;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api")
public class ListingSearchResource {

    @Inject
    ListingService listingService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(RestApiPathEnum.Constants.LISTING_SEARCH)
    public Response get(@BeanParam ListingSearchParamsVM listingSearchParamsVM) {

        // following properties are optional on client side and are filled with default here befor service validation
        if (listingSearchParamsVM.minPrice == null || listingSearchParamsVM.minPrice.isEmpty()) {
            listingSearchParamsVM.minPrice = "0";
        }
        if (listingSearchParamsVM.maxPrice == null || listingSearchParamsVM.maxPrice.isEmpty()) {
            listingSearchParamsVM.maxPrice = String.valueOf(Integer.MAX_VALUE);
        }
        if (listingSearchParamsVM.minArea == null || listingSearchParamsVM.minArea.isEmpty()) {
            listingSearchParamsVM.minArea = "0";
        }
        if (listingSearchParamsVM.maxArea == null || listingSearchParamsVM.maxArea.isEmpty()) {
            listingSearchParamsVM.maxArea = String.valueOf(Integer.MAX_VALUE);
        }

        return Response.ok(listingService.searchListings(listingSearchParamsVM)).build();
    }
}
