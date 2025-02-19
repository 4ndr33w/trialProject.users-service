package ru.authorization.auth.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "tokenvault")
public class TokenArchive {

    @Id
    @Column(name = "id", columnDefinition = "bigserial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "token")
    private String token;

    @Column(name = "userid")
    private long userid;

    @Column(name = "created")
    private Date created;

    @Column(name = "expired")
    private Date expired;

    @Column(name = "username")
    private String username;
}
