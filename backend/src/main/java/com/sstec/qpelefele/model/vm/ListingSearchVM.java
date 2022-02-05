package com.sstec.qpelefele.model.vm;

import com.sstec.qpelefele.model.TypescriptSerializable;
import com.sstec.qpelefele.model.dto.ListingDTO;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.ArrayList;
import java.util.List;

@TypescriptSerializable
@RegisterForReflection
public class ListingSearchVM {
    public List<ListingDTO> listings = new ArrayList<ListingDTO>();
    public Long total = 0L;
    public Integer numberOfPages = 0;

    public ListingSearchVM(List<ListingDTO> listings, Long total, Integer numberOfPages) {
        this.listings = listings;
        this.total = total;
        this.numberOfPages = numberOfPages;
    }
}
