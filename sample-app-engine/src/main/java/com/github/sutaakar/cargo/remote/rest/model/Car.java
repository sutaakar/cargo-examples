package com.github.sutaakar.cargo.remote.rest.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Simple car model.
 */
@XmlRootElement
public class Car {

    private String name;

    public Car() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
