package com.example.youtubeclonezti.models;

import com.example.youtubeclonezti.models.enums.ERole;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;

@Document(collection = "users")
public class User {

    @Id
    private ObjectId id;

    @NotBlank
    @Size(max = 50)
    @Email(message = "Email should be valid")
    @Indexed(unique = true)
    private String email;

    @NotBlank
    @Size(min = 6, max = 40, message
            = "password must be at least 6 characters long")
    @JsonProperty( value = "password", access = JsonProperty.Access.WRITE_ONLY)
    private String password;


    @NotBlank
    @Size(min = 3, max = 20)
    @Indexed(unique = true)
    private String username;

    private boolean isActive = true;

    private ERole role;

    private UserMeta meta;

    private String resetPasswordToken;

    private Date resetPasswordExpires;

    public User() {
    }

    public User(String email,
                String password,
                String username) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.meta = new UserMeta();
    }


    public String getId() {
        return id.toString();
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserMeta getMeta() {
        return meta;
    }

    public void setMeta(UserMeta meta) {
        this.meta = meta;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public Date getResetPasswordExpires() {
        return resetPasswordExpires;
    }

    public void setResetPasswordExpires(Date resetPasswordExpires) {
        this.resetPasswordExpires = resetPasswordExpires;
    }

    public ERole getRole() {
        return role;
    }

    public void setRole(ERole role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) &&
                email.equals(user.email) &&
                username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, username);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
