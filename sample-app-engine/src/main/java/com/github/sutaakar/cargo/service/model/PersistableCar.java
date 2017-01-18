package com.github.sutaakar.cargo.service.model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Simple car model.
 */
@Entity
public class PersistableCar {

    @Id
    private String owner;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
