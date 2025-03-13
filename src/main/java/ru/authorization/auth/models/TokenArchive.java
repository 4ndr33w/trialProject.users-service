package ru.authorization.auth.models;

import jakarta.persistence.*;

import java.util.Date;

@Entity
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

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public long getUserid() {
        return userid;
    }
    public void setUserid(long userid) {
        this.userid = userid;
    }
    public Date getCreated() {
        return created;
    }
    public void setCreated(Date created) {
        this.created = created;
    }
    public Date getExpired() {
        return expired;
    }
    public void setExpired(Date expired) {
        this.expired = expired;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "TokenArchive{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", userid=" + userid +
                ", created=" + created +
                ", expired=" + expired +
                ", username='" + username + '\'' +
                '}';
    }
}
