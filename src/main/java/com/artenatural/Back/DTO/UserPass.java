package com.artenatural.Back.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserPass {
    private String username;
    private String password;
    private String role;
    private Date birthdate;
    private String mail;
}
