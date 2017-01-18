package com.github.sutaakar.cargo.remote.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import com.github.sutaakar.cargo.remote.rest.model.Car;
import com.github.sutaakar.cargo.service.CarService;
import com.github.sutaakar.cargo.service.CarServiceLocator;
import com.github.sutaakar.cargo.service.model.PersistableCar;

@Path("car")
public class CarRestResource {

    private CarService carService = CarServiceLocator.getCarService();

    @GET
    @Produces({MediaType.APPLICATION_XML})
    public Response getCar(@Context SecurityContext context) {
        String owner = context.getUserPrincipal().getName();

        PersistableCar persistableCar = carService.getCarByOwner(owner);
        if(persistableCar == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        Car car = mapCar(persistableCar);
        return Response.status(Status.OK).entity(car).build();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_XML})
    public Response putCar(@Context SecurityContext context, Car car) {
        String owner = context.getUserPrincipal().getName();

        PersistableCar persistableCar = mapPersistableCar(car, owner);
        carService.putCar(persistableCar);

        return Response.status(Status.CREATED).build();
    }

    @DELETE
    @Consumes({MediaType.APPLICATION_XML})
    public Response deleteCar(@Context SecurityContext context) {
        String owner = context.getUserPrincipal().getName();

        carService.removeCar(owner);

        return Response.status(Status.NO_CONTENT).build();
    }

    /* Helper methods */

    private PersistableCar mapPersistableCar(Car car, String owner) {
        PersistableCar persistableCar = new PersistableCar();
        persistableCar.setName(car.getName());
        persistableCar.setOwner(owner);
        return persistableCar;
    }

    private Car mapCar(PersistableCar persistableCar) {
        Car car = new Car();
        car.setName(persistableCar.getName());
        return car;
    }
}
