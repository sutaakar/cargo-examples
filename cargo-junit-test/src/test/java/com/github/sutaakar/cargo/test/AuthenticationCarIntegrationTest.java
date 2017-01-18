package com.github.sutaakar.cargo.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
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
public class AuthenticationCarIntegrationTest {

    private static final String appUrl = System.getProperty("sample.app.url");

    private LocalContainer container;

    @Parameterized.Parameters(name = "{index}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{{new TomcatContainerProvider()}, {new WildFlyContainerProvider()}});
    }

    @Parameterized.Parameter
    public ContainerProvider provider;

    @Before
    public void setUp() throws MalformedURLException {
        container = provider.createLocalContainer();
        container.start();

        // Wait until the application properly starts
        URLDeployableMonitor monitor = new URLDeployableMonitor(new URL(appUrl));
        DeployerWatchdog watchdog = new DeployerWatchdog(monitor);
        watchdog.watch(true);
    }

    @After
    public void tearDown() {
        container.stop();
    }

    @Test
    public void testUnsuccessfulLogin() {
        Client httpClient = new ResteasyClientBuilder()
                .establishConnectionTimeout(10, TimeUnit.SECONDS)
                .socketTimeout(10, TimeUnit.SECONDS)
                .register(new Authenticator("john", "john"))
                .build();

        Response response = null;
        try {
            response = httpClient.target(appUrl).request(MediaType.APPLICATION_XML_TYPE).get();
            Assert.assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        } finally {
            if(response != null) {
                response.close();
            }
            httpClient.close();
        }
    }

    @Test
    public void testUnauthorizedLogin() {
        Client httpClient = new ResteasyClientBuilder()
                .establishConnectionTimeout(10, TimeUnit.SECONDS)
                .socketTimeout(10, TimeUnit.SECONDS)
                .register(new Authenticator("mary", "mary1234"))
                .build();

        Response response = null;
        try {
            response = httpClient.target(appUrl).request(MediaType.APPLICATION_XML_TYPE).get();
            Assert.assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        } finally {
            if(response != null) {
                response.close();
            }
            httpClient.close();
        }
    }
}
