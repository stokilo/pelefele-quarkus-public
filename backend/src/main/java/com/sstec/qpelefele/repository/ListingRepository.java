package com.sstec.qpelefele.repository;

import com.sstec.qpelefele.model.Listing;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ListingRepository implements PanacheRepository<Listing> {

    public Listing findByTitle(String title){
        return find("title", title).firstResult();
    }

}
