package com.sstec.qpelefele.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "LISTING_S3_ASSET")
public class ListingS3Asset extends PanacheEntity {

    @Column(name = "FILE_NAME")
    public String fileName;

    @Column(name = "IS_COVER")
    public Boolean isCover;

    @ManyToOne(fetch = FetchType.LAZY)
    public Listing listing;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListingS3Asset that = (ListingS3Asset) o;
        return Objects.equals(fileName, that.fileName) && Objects.equals(isCover, that.isCover) && Objects.equals(listing, that.listing);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, isCover, listing);
    }
}
