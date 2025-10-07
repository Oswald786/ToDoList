package com.todolist.adaptors.persistence.jpa;

import io.micronaut.http.annotation.Get;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor

@Entity
@Table(name="USERS")
public class userEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    @Getter
    @Setter
    private Long id;

    @Column(name = "USERNAME")
    @Getter
    @Setter
    private String userName;

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
    private String EMAIL;
}
