package com.github.sutaakar.cargo.test;

import java.io.File;
import java.net.MalformedURLException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.github.sutaakar.cargo.test.util.Authenticator;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CarIntegrationTest {

    private static final String BMW = "<car><name>BMW</name></car>";

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.createFromZipFile(WebArchive.class, new File("../sample-app/target/sample-app-1.0.0-wildfly.war"));
    }

    @Test
    @RunAsClient
    public void testCarHandling(@ArquillianResteasyResource final WebTarget webTarget) throws MalformedURLException, InterruptedException {
        String car = getCar(webTarget);
        Assert.assertNull("Some response was returned!", car);

        insertCar(BMW, webTarget);

        car = getCar(webTarget);
        Assert.assertTrue("BMW was not returned!", car.contains(BMW));

        removeCar(webTarget);

        car = getCar(webTarget);
        Assert.assertNull("Some response was returned!", car);
    }

    /* Helper methods */

    private String getCar(WebTarget webTarget) {
        Response response = null;
        try {
            response = webTarget.path("car").register(new Authenticator("john", "john123")).request(MediaType.APPLICATION_XML_TYPE).get();

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

    private void insertCar(String car, WebTarget webTarget) {
        Response response = null;
        try {
            response = webTarget.path("car").register(new Authenticator("john", "john123")).request(MediaType.APPLICATION_XML_TYPE).put(Entity.entity(car, MediaType.APPLICATION_XML_TYPE));
        } finally {
            if(response != null) {
                response.close();
            }
        }
    }

    private void removeCar(WebTarget webTarget) {
        Response response = null;
        try {
            response = webTarget.path("car").register(new Authenticator("john", "john123")).request(MediaType.APPLICATION_XML_TYPE).delete();
        } finally {
            if(response != null) {
                response.close();
            }
        }
    }
}
