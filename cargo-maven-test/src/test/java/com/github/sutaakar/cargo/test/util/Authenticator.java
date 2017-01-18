package com.github.sutaakar.cargo.test.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.DatatypeConverter;

public class Authenticator implements ClientRequestFilter {

    private final String user;
    private final String password;

    public Authenticator(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public void filter(ClientRequestContext requestContext) throws IOException {
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();

        String token = this.user + ":" + this.password;
        try {
            String authentication = "Basic " + DatatypeConverter.printBase64Binary(token.getBytes("UTF-8"));
            headers.add("Authorization", authentication);
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("Cannot encode with UTF-8", ex);
        }
    }
}
