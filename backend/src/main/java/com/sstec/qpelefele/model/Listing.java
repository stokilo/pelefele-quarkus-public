package com.sstec.qpelefele.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "LISTING")
public class Listing extends PanacheEntity {

    @Column(name = "TITLE")
    public String title;

    @Column(name = "PRICE", precision = 10, scale = 2)
    public BigDecimal price;

    @Column(name = "PROPERTY_TYPE")
    public ListingPropertyType propertyType;

    @Column(name = "PURPOSE")
    public ListingPurpose purpose;

    @Column(name = "AREA", precision = 10, scale = 2)
    public BigDecimal area;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "OIDC_USER_ID", nullable = false)
    public OIDCUser user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LOCATION_ID", nullable = false)
    public Location location;

    @OneToMany(mappedBy = "listing",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    public List<ListingS3Asset> assets = new ArrayList<>();

    public void addS3Asset(ListingS3Asset listingS3Asset) {
        assets.add(listingS3Asset);
        listingS3Asset.listing = this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Listing listing = (Listing) o;
        return Objects.equals(title, listing.title) && Objects.equals(price, listing.price) && propertyType == listing.propertyType && purpose == listing.purpose && Objects.equals(area, listing.area) && Objects.equals(user, listing.user) && Objects.equals(location, listing.location) && Objects.equals(assets, listing.assets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, price, propertyType, purpose, area, user, location, assets);
    }
}
