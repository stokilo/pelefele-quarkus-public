package com.sstec.qpelefele.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;

@Entity
@Table(name = "LOCATION")
public class Location extends PanacheEntity{

    @Column(name = "STREET")
    public String street;

    @Column(name = "CITY")
    public String city;

    @Column(name = "WOJ")
    public Integer woj;

    @Column(name = "POW")
    public Integer pow;

    @Column(name = "GMI")
    public Integer gmi;

    @Column(name = "MZ")
    public Integer mz;

    @Column(name = "RM")
    public Integer rm;

    @Column(name = "LOCATION_NAME_V1")
    public String locationNameV1;

    @Column(name = "LOCATION_NAME_V2")
    public String locationNameV2;

    @Column(name = "LOCATION_NAME_V3")
    public String locationNameV3;

    @Column(name = "LOCATION_ID")
    public String locationId;

    @Column(name = "FULL_LOCATION_ID")
    public String fullLocationId;

}
