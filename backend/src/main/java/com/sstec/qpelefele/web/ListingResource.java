package com.sstec.qpelefele.web;

import com.sstec.qpelefele.config.OIDCRoleType;
import com.sstec.qpelefele.model.Listing;
import com.sstec.qpelefele.model.dto.ListingDTO;
import com.sstec.qpelefele.service.ListingService;
import io.quarkus.security.Authenticated;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/api")
public class ListingResource {

    @Inject
    ListingService listingService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(RestApiPathEnum.Constants.LISTINGS)
    @RolesAllowed({OIDCRoleType.OIDCRole.REGULAR_USER})
    public Response create(ListingDTO pListingDTO) {
        ListingDTO listing = listingService.create(pListingDTO);
        return Response.created(URI.create(RestApiPathEnum.Constants.LISTINGS + "/" + listing.id)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(RestApiPathEnum.Constants.LISTINGS)
    @Authenticated
    public Response get(@QueryParam("pageNumber") @NotNull Integer pageNumber,
                        @QueryParam("pageSize") @NotNull Integer pageSize) {
        return Response.ok(listingService.fetchUserListings(pageNumber, pageSize)).build();
    }
}
