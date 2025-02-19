package ru.authorization.auth.models;

import java.util.Date;
import java.util.List;
import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import ru.authorization.auth.models.enums.UserStatus;
import ru.authorization.auth.models.Dtos.UserDto;

@Getter
@Setter
@Entity
@RequiredArgsConstructor
@Table(name = "users", schema = "astonauth")
public class UserModel implements UserDetails {
    @Id
    @Column(name = "id", columnDefinition = "bigserial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", columnDefinition = "text", nullable = false)
    private String name;

    @Column(name = "email", columnDefinition = "text", nullable = false)
    private String email;

    @Column(name = "password", columnDefinition = "text", nullable = false)
    private String password;

    @Column(name = "lastname", columnDefinition = "text")
    private String lastName;

    @Column(name = "phone", columnDefinition = "text")
    private String phone;

    @Column(name = "created", columnDefinition = "timestamptz")
    private Date created = new Date();

    @Column(name = "updated", columnDefinition = "timestamptz")
    private Date updated = new Date();

    @Column(name = "image", columnDefinition = "bytea")
    private byte[] image;

    @Column(name = "lastlogindate", columnDefinition = "timestamptz")
    private Date lastLoginDate = new Date();

    @Column(name = "userstatus")
    @Enumerated(jakarta.persistence.EnumType.ORDINAL)
    private UserStatus userStatus = UserStatus.USER;

    @Column(name = "username", columnDefinition = "text")

    //Для реализации UserDetails
    //из комплекта Spring.Security
    private String username;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    public UserDto toDto() {
        return new UserDto(this);

    }
}
