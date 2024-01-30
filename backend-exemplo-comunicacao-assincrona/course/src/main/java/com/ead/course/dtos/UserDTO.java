package com.ead.course.dtos;

import com.ead.course.enums.UserStatus;
import com.ead.course.enums.UserType;
import lombok.Data;

import java.util.UUID;

@Data
public class UserDTO {

    private UUID userId;
    private String username;
    private String email;
    private String fullName;
    private UserType userType;
    private UserStatus userStatus;
    private String phoneNumber;
    private String cpf;
    private String imageUrl;

}
