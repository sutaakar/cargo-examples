package com.github.sutaakar.cargo.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.github.sutaakar.cargo.test.provider.ContainerProvider;
import com.github.sutaakar.cargo.test.provider.TomcatContainerProvider;
import com.github.sutaakar.cargo.test.provider.WildFlyContainerProvider;
import com.github.sutaakar.cargo.test.util.Authenticator;
import org.codehaus.cargo.container.LocalContainer;
import org.codehaus.cargo.container.deployer.URLDeployableMonitor;
import org.codehaus.cargo.container.spi.deployer.DeployerWatchdog;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class CarIntegrationTest {

    private static final String appUrl = System.getProperty("sample.app.url", "http://localhost:8080/sample-app/rest/car");
    private static final String BMW = "<car><name>BMW</name></car>";

    private Client httpClient;
    private LocalContainer container;

    @Parameterized.Parameters(name = "{index}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{{new TomcatContainerProvider()}, {new WildFlyContainerProvider()}});
    }

    @Parameterized.Parameter
    public ContainerProvider provider;

    @Before
    public void setUp() throws MalformedURLException {
        httpClient = new ResteasyClientBuilder()
                .establishConnectionTimeout(10, TimeUnit.SECONDS)
                .socketTimeout(10, TimeUnit.SECONDS)
                .register(new Authenticator("john", "john123"))
                .build();

        container = provider.createLocalContainer();
        container.start();

        // Wait until the application properly starts
        URLDeployableMonitor monitor = new URLDeployableMonitor(new URL(appUrl));
        DeployerWatchdog watchdog = new DeployerWatchdog(monitor);
        watchdog.watch(true);
    }

    @After
    public void tearDown() {
        httpClient.close();

        container.stop();
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
            response = httpClient.target(appUrl).request(MediaType.APPLICATION_XML_TYPE).get();

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
            response = httpClient.target(appUrl).request(MediaType.APPLICATION_XML_TYPE).put(Entity.entity(car, MediaType.APPLICATION_XML_TYPE));
        } finally {
            if(response != null) {
                response.close();
            }
        }
    }

    private void removeCar() {
        Response response = null;
        try {
            response = httpClient.target(appUrl).request(MediaType.APPLICATION_XML_TYPE).delete();
        } finally {
            if(response != null) {
                response.close();
            }
        }
    }
}
