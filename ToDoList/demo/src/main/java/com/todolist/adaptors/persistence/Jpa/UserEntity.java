package com.todolist.adaptors.persistence.Jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor

@Entity
@Table(name="USERS")
public class UserEntity {

    //need to work on getitng the id randomly hen creating objects
    @Column(name = "USER_ID")
    @Getter
    @Setter
    private Long id;

    @Id
    @Column(name = "USERNAME",unique = true)
    @Getter
    @Setter
    private String username;

    @Column(name = "PASSWORD")
    @Getter
    @Setter
    private String password;

    @Column(name = "ROLE")
    @Getter
    @Setter
    private String role;

    @Column(name = "EMAIL")
    @Getter
    @Setter
    private String email;
}
