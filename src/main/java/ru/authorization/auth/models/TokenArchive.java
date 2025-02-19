package ru.authorization.auth.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "tokenvault", schema = "astonauth")
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
    private long created;

    @Column(name = "expired")
    private long expired;

    @Column(name = "username")
    private String username;
}
