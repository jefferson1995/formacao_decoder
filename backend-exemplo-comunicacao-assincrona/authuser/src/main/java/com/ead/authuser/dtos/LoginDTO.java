package com.ead.authuser.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
