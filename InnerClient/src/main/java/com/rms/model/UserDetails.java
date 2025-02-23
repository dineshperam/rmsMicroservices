package com.rms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class UserDetails {

    private int userid;

    private String username;

    private String email;

    private String firstName;


    private String lastName;

    private String mobileNo;

    private String address;

    private String role;

    private String passwordHash;

    private String password;

    private int managerId;
    
    private boolean isActive;

    private boolean firstLogin;
}
