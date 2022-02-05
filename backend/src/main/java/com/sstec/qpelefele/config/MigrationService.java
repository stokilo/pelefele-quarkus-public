package com.sstec.qpelefele.config;

import io.quarkus.runtime.StartupEvent;
import org.flywaydb.core.Flyway;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class MigrationService {
    @Inject
    Flyway flyway;


    void onStart(@Observes StartupEvent ev) {
//        flyway.clean();
//        flyway.migrate();
//        System.out.println(flyway.info().current().getVersion().toString());
    }
}
