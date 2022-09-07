package com.mdsdev.spring.security.client.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PasswordModel {
    private String email;
    private String oldPassword;
    private String newPassword;
}
