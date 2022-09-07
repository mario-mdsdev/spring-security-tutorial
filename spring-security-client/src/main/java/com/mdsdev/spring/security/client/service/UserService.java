package com.mdsdev.spring.security.client.service;

import com.mdsdev.spring.security.client.entity.User;
import com.mdsdev.spring.security.client.entity.VerificationToken;
import com.mdsdev.spring.security.client.model.UserModel;

import java.util.Optional;

public interface UserService {
    User register(UserModel userModel);

    void saveVerificationToken(User user, String token);

    String validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);

    User findByEmail(String email);

    void createPasswordResetToken(User user, String token);

    String validatePasswordResetToken(String token);

    Optional<User> getByPasswordResetToken(String token);

    void changePassword(User user, String newPassword);

    boolean isValidOldPassword(User user, String oldPassword);
}
