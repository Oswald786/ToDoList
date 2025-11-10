package com.todolist.auth;

import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import com.todolist.Models.userDetailsModel;
import io.micronaut.core.cli.exceptions.ParseException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.render.BearerAccessRefreshToken;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import static io.micronaut.http.HttpStatus.OK;
import static io.micronaut.http.HttpStatus.UNAUTHORIZED;
import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest
public class JwtAuthenticationTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    AuthAdaptorService authAdaptorService;

    @MockBean(AuthAdaptorService.class)
    AuthAdaptorService authAdaptorService() {
        AuthAdaptorService authAdaptorService = mock(AuthAdaptorService.class);
        PasswordHasher hasher = new PasswordHasher();

        userDetailsModel testUser = new userDetailsModel();
        testUser.setUserName("sherlock");
        testUser.setPassword(hasher.hashPassword("password"));
        testUser.setRole("USER");
        testUser.setEMAIL("sherlock@example.com");

        when(authAdaptorService.findUser(eq("sherlock"))).thenReturn(testUser);

        return authAdaptorService;
    }

    // 🧩 1. Unauthorized user should not access secured endpoint
    @Test
    void accessingSecuredEndpointWithoutToken_ReturnsUnauthorized() {
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/test-secured/test").accept(TEXT_PLAIN));
        });

        assertEquals(UNAUTHORIZED, e.getStatus());
    }

    // 🧩 2. Successful authentication issues JWT
    @Test
    void uponSuccessfulAuthenticationAJsonWebTokenIsIssuedToTheUser() throws Exception {
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("sherlock", "password");
        HttpRequest<?> loginRequest = HttpRequest.POST("/login", creds);

        HttpResponse<BearerAccessRefreshToken> rsp =
                client.toBlocking().exchange(loginRequest, BearerAccessRefreshToken.class);

        assertEquals(OK, rsp.getStatus());
        BearerAccessRefreshToken tokenResponse = rsp.body();
        assertEquals("sherlock", tokenResponse.getUsername());
        assertNotNull(tokenResponse.getAccessToken());
    }
}
