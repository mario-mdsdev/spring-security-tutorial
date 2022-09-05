package com.mdsdev.spring.security.client.service;

import com.mdsdev.spring.security.client.entity.User;
import com.mdsdev.spring.security.client.entity.VerificationToken;
import com.mdsdev.spring.security.client.model.UserModel;
import com.mdsdev.spring.security.client.repository.UserRepository;
import com.mdsdev.spring.security.client.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(UserModel userModel) {
        final User user = User.builder()
                .firstName(userModel.getFirstName())
                .lastName(userModel.getLastName())
                .email(userModel.getEmail())
                .role("USER")
                .password(passwordEncoder.encode(userModel.getPassword()))
                .build();
        userRepository.save(user);
        return user;
    }

    @Override
    public void saveVerificationToken(User user, String token) {
        final VerificationToken verificationToken = new VerificationToken(user, token);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateVerificationToken(String token) {
        final VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) return "invalid token";

        final User user = verificationToken.getUser();
        final Calendar calendar = Calendar.getInstance();

        final long expirationTimeToken = verificationToken.getExpirationTime().getTime();
        final long currentMillis = calendar.getTimeInMillis();

        if (expirationTimeToken - currentMillis <= 0) {
            verificationTokenRepository.delete(verificationToken);
            return "expired token";
        }

        user.setEnabled(true);
        userRepository.save(user);

        return "User verified successfully!";
    }

}
