package com.todolist.auth;

import com.todolist.Models.UserDetailsModel;
import com.todolist.adaptors.persistence.Jpa.UserEntity;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.render.BearerAccessRefreshToken;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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

        UserDetailsModel testUser = new UserDetailsModel();
        testUser.setUsername("sherlock");
        testUser.setPassword(hasher.hashPassword("password"));
        testUser.setRole("USER");
        testUser.setEmail("sherlock@example.com");

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
        assertNotNull(tokenResponse.getAccessToken());
    }

    @Test
    @DisplayName("if username is null bad request raised")
    void login_throws_When_Username_Null() {
        // Arrange
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(null, "password");
        HttpRequest<?> request = HttpRequest.POST("/login", creds);

        // Act
        HttpClientResponseException exception =
                assertThrows(HttpClientResponseException.class, () ->
                        client.toBlocking().exchange(request, BearerAccessRefreshToken.class));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        String body = exception.getResponse().getBody(String.class).orElse("");
        assertTrue(body.contains("usernamePasswordCredentials.username: must not be blank"));
    }

    @Test
    @DisplayName("if Password is null http exception raised and http bad requests raised as well")
    void login_throws_when_password_is_null(){
        // Arrange
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("testUser", null);
        HttpRequest<?> request = HttpRequest.POST("/login", creds);

        // Act
        HttpClientResponseException exception =
                assertThrows(HttpClientResponseException.class, () ->
                        client.toBlocking().exchange(request, BearerAccessRefreshToken.class));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    @DisplayName("Tests that an invalid password for a valid user results in an unauthorised response.")
    void Test_Invalid_Password_For_Valid_User_Returns_Unauthorized(){
        //Arrange
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("sherlock", "wrongPassword");
        HttpRequest<?> request = HttpRequest.POST("/login", creds);
        UserDetailsModel userDetailsModel = new UserDetailsModel();
        userDetailsModel.setUsername("sherlock");
        userDetailsModel.setPassword("password");
        when(authAdaptorService.findUser(eq("sherlock"))).thenReturn(userDetailsModel);

        //Act
        HttpClientResponseException exception =
                assertThrows(HttpClientResponseException.class, () ->
                        client.toBlocking().exchange(request, BearerAccessRefreshToken.class));

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    @DisplayName("Returns 200 OK when valid user credentials are provided")
    void Test_Valid_Password_For_Valid_User_Returns_authorized(){
        // Arrange
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("sherlock", "password");
        PasswordHasher hasher = new PasswordHasher();
        HttpRequest<?> request = HttpRequest.POST("/login", creds);

        UserDetailsModel userDetailsModel = new UserDetailsModel();
        userDetailsModel.setUsername("sherlock");
        userDetailsModel.setPassword(hasher.hashPassword("password"));
        userDetailsModel.setRole("USER");

        when(authAdaptorService.findUser(eq("sherlock"))).thenReturn(userDetailsModel);

        // Act
        HttpResponse<BearerAccessRefreshToken> response =
                client.toBlocking().exchange(request, BearerAccessRefreshToken.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body().getAccessToken());
    }
}
