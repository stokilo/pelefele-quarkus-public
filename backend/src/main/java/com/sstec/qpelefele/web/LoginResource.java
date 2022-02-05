package com.sstec.qpelefele.web;

import com.sstec.qpelefele.model.OIDCUser;
import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api")
public class LoginResource {

    private static final Logger log = Logger.getLogger(LoginResource.class);

    @Inject
    JsonWebToken accessToken;

    @Inject
    @Claim(standard = Claims.sub)
    String subject;

    @Inject
    @Claim(standard = Claims.email)
    String email;

    @Inject
    @Claim(standard = Claims.preferred_username)
    String preferredUsername;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(RestApiPathEnum.Constants.LOGIN)
    @Transactional
    @Authenticated
    public Response generatePreSignUrl() {

        try {
            OIDCUser existingUser = OIDCUser.findByUUID(subject);
            if (existingUser == null) {
                String username = preferredUsername == null ? accessToken.getClaim("cognito:username") : preferredUsername;
                OIDCUser oidcUser = new OIDCUser();
                oidcUser.email = email;
                oidcUser.username = username;
                oidcUser.oidcUUID = subject;
                oidcUser.persist();
            }
        } catch (Exception exception) {
            log.error(exception);
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return Response.ok().build();
    }

}
