package ru.authorization.auth.models;

import java.util.Date;
import java.util.List;
import java.util.Collection;

import jakarta.persistence.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import ru.authorization.auth.models.enums.UserStatus;

@Entity
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
    //@Builder.Default
    private Date created = new Date();

    @Column(name = "updated", columnDefinition = "timestamptz")
    //@Builder.Default
    private Date updated = new Date();

    @Column(name = "image", columnDefinition = "bytea")
    private byte[] image;

    @Column(name = "lastlogindate", columnDefinition = "timestamptz")
    //@Builder.Default
    private Date lastLoginDate = new Date();

    @Column(name = "userstatus")
    @Enumerated(EnumType.ORDINAL)
    //@Builder.Default
    private UserStatus userStatus = UserStatus.USER;

    @Column(name = "username", columnDefinition = "text")

    //Для реализации UserDetails
    //из комплекта Spring.Security
    private String username;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    public UserModel () {
        this.id = 0;

    }
    public UserModel (long id, String name, String email, String password, String lastName, String phone, Date created, Date updated, byte[] image, Date lastLoginDate, UserStatus userStatus, String username) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.lastName = lastName;
        this.phone = phone;
        this.created = created;
        this.updated = updated;
        this.image = image;
        this.lastLoginDate = lastLoginDate;
        this.userStatus = userStatus;
        this.username = email;

    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    @Override
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public Date getCreated() {
        return created;
    }
    public void setCreated(Date created) {
        this.created = created;
    }
    public Date getUpdated() {
        return updated;
    }
    public void setUpdated(Date updated) {
        this.updated = updated;
    }
    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        this.image = image;
    }
    public Date getLastLoginDate() {
        return lastLoginDate;
    }
    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
    public UserStatus getUserStatus() {
        return userStatus;
    }
    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }
    @Override
    public String getUsername() {
        return email;
    }
    public void setUsername(String username) {
        this.username = username;
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

    public static class Builder {
        private long id;
        private String name;
        private String email;
        private String password;
        private String lastName;
        private String phone;
        private Date created = new Date();
        private Date updated = new Date();
        private byte[] image;
        private Date lastLoginDate = new Date();
        private UserStatus userStatus = UserStatus.USER;
        private String username;

        public Builder() {}

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder created(Date created) {
            this.created = created;
            return this;
        }

        public Builder updated(Date updated) {
            this.updated = updated;
            return this;
        }

        public Builder image(byte[] image) {
            this.image = image;
            return this;
        }

        public Builder lastLoginDate(Date lastLoginDate) {
            this.lastLoginDate = lastLoginDate;
            return this;
        }

        public Builder userStatus(UserStatus userStatus) {
            this.userStatus = userStatus;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public UserModel build() {
            return new UserModel(
                    id,
                    name,
                    email,
                    password,
                    lastName,
                    phone,
                    created,
                    updated,
                    image,
                    lastLoginDate,
                    userStatus,
                    username
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
