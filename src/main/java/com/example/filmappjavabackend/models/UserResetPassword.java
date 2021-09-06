package com.example.filmappjavabackend.models;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserResetPassword {

    @NotBlank
    @Size(min = 6, max = 40, message
            = "password must be at least 6 characters long")
    private String password;


    public UserResetPassword() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
