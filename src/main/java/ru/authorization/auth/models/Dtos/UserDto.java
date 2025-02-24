package ru.authorization.auth.models.Dtos;

import lombok.*;

import java.util.Date;

import ru.authorization.auth.models.UserModel;
import ru.authorization.auth.models.enums.UserStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@RequiredArgsConstructor
public class UserDto {
    private long id;
    private String name;
    private String lastName;
    private String email;
    private UserStatus userStatus;
    private String phone;
    private  byte[] image;
    private Date created;

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", userStatus=" + userStatus +
                ", phone='" + phone + '\'' +
                ", image=" + image +
                ", created=" + created +
                '}';
    }
}
