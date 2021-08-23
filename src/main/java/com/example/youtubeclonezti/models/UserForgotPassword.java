package com.example.youtubeclonezti.models;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserForgotPassword {

    @NotBlank
    @Size(max = 50)
    @Email(message = "Email should be valid")
    private String email;


    public UserForgotPassword() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
