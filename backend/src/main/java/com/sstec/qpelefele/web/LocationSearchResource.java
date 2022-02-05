package com.sstec.qpelefele.web;

import com.sstec.qpelefele.model.vm.LocationSearchVM;
import com.sstec.qpelefele.model.dto.LocationDTO;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Path("/api")
public class LocationSearchResource {

    @Inject
    EntityManager entityManager;

    @ConfigProperty(name= "quarkus.hibernate-orm.database.default-schema")
    String defaultSchema;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(RestApiPathEnum.Constants.LOCATION_SEARCH)
    @SuppressWarnings("unchecked")
    public Response get(@QueryParam("term") String term) {

        LocationSearchVM locationSearchVM = new LocationSearchVM();
        System.out.println("term " + term);
        if (term.length() > 0) {
            int count = term.split(" ").length;
            String tsquerySearchTerm = term.trim().replaceAll(" ", ":* & ") + (count >= 1 ? ":*" : "");
            System.out.println("[" + tsquerySearchTerm + "]");

            if (tsquerySearchTerm.length() > 0) {
                String nativeQuery = String.format("SELECT LOCATION_NAME_V1, ID from %s.LOCATION WHERE document_tokens @@ to_tsquery('%s') limit 10;",
                        defaultSchema, tsquerySearchTerm);
                List<Object[]> result = (List<Object[]>) this.entityManager.createNativeQuery(nativeQuery).getResultList();

                locationSearchVM.locations = result.stream().map(val -> {
                    LocationDTO locationDTO = new LocationDTO();
                    locationDTO.location = (String) val[0];
                    locationDTO.id = val[1].toString();
                    return locationDTO;
                }).collect(Collectors.<LocationDTO>toList());
            }
        }

        return Response.ok(locationSearchVM).build();
    }
}
