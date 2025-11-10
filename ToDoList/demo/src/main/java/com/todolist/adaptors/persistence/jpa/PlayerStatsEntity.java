package com.todolist.adaptors.persistence.jpa;

import io.micronaut.http.annotation.Get;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="PLAYERSTATS")
@Getter
@Setter
public class PlayerStatsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PLAYER_PROFILE_ID")
    private Long playerProfileId;

    @Column(name = "PLAYER_USERNAME", nullable = false,unique = true)
    private String playerUsername;

    @Column(name = "PLAYER_LEVEL", nullable = false)
    private Integer playerLevel;

    @Column(name = "PLAYER_XP", nullable = false)
    private Integer playerXp;

    @Column(name = "XP_TO_NEXT_LEVEL", nullable = false)
    private Integer xpToNextLevel;


}
