package com.mdsdev.spring.security.client.service;

import com.mdsdev.spring.security.client.entity.User;
import com.mdsdev.spring.security.client.model.UserModel;

public interface UserService {
    User registerUser(UserModel userModel);

    void saveVerificationToken(User user, String token);

    String validateVerificationToken(String token);
}
