package com.github.sutaakar.cargo.test;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.github.sutaakar.cargo.test.util.Authenticator;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CarIntegrationTest {

    private static final String URL = System.getProperty("sample.app.url");
    private static final String BMW = "<car><name>BMW</name></car>";

    private Client httpClient;

    @Before
    public void setUp() {
        httpClient = new ResteasyClientBuilder()
                .establishConnectionTimeout(10, TimeUnit.SECONDS)
                .socketTimeout(10, TimeUnit.SECONDS)
                .register(new Authenticator("john", "john123"))
                .build();
    }

    @After
    public void tearDown() {
        httpClient.close();
    }

    @Test
    public void testCarHandling() {
        String car = getCar();
        Assert.assertNull("Some car was returned!", car);

        insertCar(BMW);

        car = getCar();
        Assert.assertTrue("BMW was not returned!", car.contains(BMW));

        removeCar();

        car = getCar();
        Assert.assertNull("Some car was returned!", car);
    }

    /* Helper methods */

    private String getCar() {
        Response response = null;
        try {
            response = httpClient.target(URL).request(MediaType.APPLICATION_XML_TYPE).get();

            if(response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                return null;
            }

            return response.readEntity(String.class);
        } finally {
            if(response != null) {
                response.close();
            }
        }
    }

    private void insertCar(String car) {
        Response response = null;
        try {
            response = httpClient.target(URL).request(MediaType.APPLICATION_XML_TYPE).put(Entity.entity(car, MediaType.APPLICATION_XML_TYPE));
        } finally {
            if(response != null) {
                response.close();
            }
        }
    }

    private void removeCar() {
        Response response = null;
        try {
            response = httpClient.target(URL).request(MediaType.APPLICATION_XML_TYPE).delete();
        } finally {
            if(response != null) {
                response.close();
            }
        }
    }
}
