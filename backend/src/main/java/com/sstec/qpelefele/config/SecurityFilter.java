package com.sstec.qpelefele.config;

import io.quarkus.arc.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@PreMatching
@Provider
@Priority(value = 10)
@ApplicationScoped
public class SecurityFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        // add here common security filtering logic
    }
}
