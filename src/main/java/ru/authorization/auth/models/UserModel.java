package ru.authorization.auth.models;

import java.util.Date;
import java.util.List;
import java.util.Collection;

import jakarta.persistence.*;
import lombok.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import ru.authorization.auth.models.enums.UserStatus;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @Enumerated(EnumType.ORDINAL)
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
    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                ", image=" + image +
                ", lastLoginDate=" + lastLoginDate +
                ", userStatus=" + userStatus +
                '}';
    }
}
