package com.todolist.auth;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;

@Serdeable
public class LoginRequestModel {
    @Getter
    @Setter
    String username;

    @Getter
    @Setter
    String password;
}
