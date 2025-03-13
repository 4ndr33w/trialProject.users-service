package ru.authorization.auth.models.Dtos;

import java.util.Date;

import ru.authorization.auth.models.enums.UserStatus;

public class UserDto {
    private long id;
    private String name;
    private String lastName;
    private String email;
    private UserStatus userStatus;
    private String phone;
    private  byte[] image;
    private Date created;

    public UserDto() {}
    public UserDto(long id, String name, String lastName, String email, UserStatus userStatus, String phone, byte[] image, Date created) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.userStatus = userStatus;
        this.phone = phone;
        this.image = image;
        this.created = created;
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
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public UserStatus getUserStatus() {
        return userStatus;
    }
    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        this.image = image;
    }
    public Date getCreated() {
        return created;
    }
    public void setCreated(Date created) {
        this.created = created;
    }

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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long id;
        private String name;
        private String lastName;
        private String email;
        private UserStatus userStatus;
        private String phone;
        private byte[] image;
        private Date created;

        public Builder() {}

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder userStatus(UserStatus userStatus) {
            this.userStatus = userStatus;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder image(byte[] image) {
            this.image = image;
            return this;
        }

        public Builder created(Date created) {
            this.created = created;
            return this;
        }

        public UserDto build() {
            return new UserDto(
                    id,
                    name,
                    lastName,
                    email,
                    userStatus,
                    phone,
                    image,
                    created
            );
        }
    }
}
