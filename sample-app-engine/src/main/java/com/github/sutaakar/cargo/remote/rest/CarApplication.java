package com.github.sutaakar.cargo.remote.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/")
public class CarApplication extends Application {

    @Override
    public Set<Object> getSingletons() {
        Set<Object> resources = new HashSet<>();
        resources.add(new CarRestResource());
        return resources;
    }
}
