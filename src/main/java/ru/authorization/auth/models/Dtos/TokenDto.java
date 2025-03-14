package ru.authorization.auth.models.Dtos;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import ru.authorization.auth.models.enums.UserStatus;

import java.util.Date;

@JsonAutoDetect
public class TokenDto {

    private String userName;
    private UserStatus userStatus;
    private String token;
    private Date createdAt;

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public UserStatus getUserStatus() {
        return userStatus;
    }
    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
