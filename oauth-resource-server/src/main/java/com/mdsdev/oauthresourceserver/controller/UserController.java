package com.mdsdev.oauthresourceserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/api/users")
    public String[] getUsers() {
        return new String[] {
                "Shabbir",
                "Nikhil",
                "Shivam"
        };
    }

}
