package com.todolist;

import com.todolist.Models.UserDetailsModel;
import com.todolist.auth.RegistrationService;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@MicronautTest
class AuthControllerTest {
    @Inject
    RegistrationService registrationService;

    @Inject
    @Client("/")
    HttpClient client;

    @MockBean(RegistrationService.class)
    RegistrationService registrationService() {
        return Mockito.mock(RegistrationService.class);
    }

//    @Inject
//    RegistrationService registrationService;

    //Tests micronaut accepts a valid request
    @Test
    @DisplayName("When client sends a valid request the controller uses the logic to return a response and http 200 ok code.")
    void testValidRegisterReturnsOk() {
        //Arrange
        UserDetailsModel model =
                new UserDetailsModel("ethan123", "password123", "USER", "ethan@example.com");

        //Act
        HttpRequest<UserDetailsModel> request = HttpRequest.POST("/v1Authentication/register", model);
        HttpResponse<String> response = client.toBlocking().exchange(request, String.class);
        ArgumentCaptor<UserDetailsModel> captor = ArgumentCaptor.forClass(UserDetailsModel.class);
        Mockito.verify(registrationService).register(captor.capture());

        //Assert
        Assertions.assertEquals(200, response.getStatus().getCode());
        UserDetailsModel userRegisteredSuccessfully = captor.getValue();
        Assertions.assertEquals(model.getUsername(), userRegisteredSuccessfully.getUsername());
        Assertions.assertEquals(model.getPassword(), userRegisteredSuccessfully.getPassword());
        Assertions.assertEquals(model.getRole(), userRegisteredSuccessfully.getRole());
        Assertions.assertEquals(model.getEmail(), userRegisteredSuccessfully.getEmail());
    }

    //Tests micronaut returns correct response with invalid request
    @Test
    @DisplayName("Returns InternalServiceError code 500 when username is null or blank")
    void testInvalidRegisterUsernameReturnsInternalServerError() {
        //Arrange
        UserDetailsModel modelNullUsername = new UserDetailsModel(null, "password123", "USER", "ethan@example.com");
        UserDetailsModel modelBlankUsername = new UserDetailsModel("", "password123", "USER", "ethan@example.com");

        //Act + Assert
        HttpRequest<UserDetailsModel> requestNullUsername = HttpRequest.POST("/v1Authentication/register", modelNullUsername);

        HttpRequest<UserDetailsModel> requestBlankUsername = HttpRequest.POST("/v1Authentication/register", modelNullUsername);

        HttpClientResponseException exceptionNullUsername = Assertions.assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(requestNullUsername, String.class));
        HttpClientResponseException exceptionBlankUsername =Assertions.assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(requestBlankUsername, String.class));
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionNullUsername.getStatus());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionBlankUsername.getStatus());
        Mockito.verify(registrationService, Mockito.never()).register(any(UserDetailsModel.class));
    }

    @Test
    void register() {
    }
}