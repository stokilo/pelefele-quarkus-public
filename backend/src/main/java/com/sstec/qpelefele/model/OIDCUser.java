package com.sstec.qpelefele.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;

@Entity
@Table(name = "OIDC_USER")
public class OIDCUser extends PanacheEntity {

    @Column(name = "USERNAME", unique = true, nullable = false)
    public String username;

    @Column(name = "EMAIL", unique = true, nullable = false)
    public String email;

    @Column(name = "OIDC_UUID", unique = true, nullable = false)
    public String oidcUUID;

    public static OIDCUser findByUUID(String oidcUUID){
        return find("oidcUUID", oidcUUID).firstResult();
    }

}
