package com.github.sutaakar.cargo.test;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.github.sutaakar.cargo.test.util.Authenticator;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.Assert;
import org.junit.Test;

public class AuthenticationCarIntegrationTest {

    private static final String URL = System.getProperty("sample.app.url");

    @Test
    public void testUnsuccessfulLogin() {
        Client httpClient = new ResteasyClientBuilder()
                .establishConnectionTimeout(10, TimeUnit.SECONDS)
                .socketTimeout(10, TimeUnit.SECONDS)
                .register(new Authenticator("john", "john"))
                .build();

        Response response = null;
        try {
            response = httpClient.target(URL).request(MediaType.APPLICATION_XML_TYPE).get();
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
            response = httpClient.target(URL).request(MediaType.APPLICATION_XML_TYPE).get();
            Assert.assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        } finally {
            if(response != null) {
                response.close();
            }
            httpClient.close();
        }
    }
}
