package br.com.mdsdev.spring.security.client.service;

import br.com.mdsdev.spring.security.client.entity.PasswordResetToken;
import br.com.mdsdev.spring.security.client.model.UserModel;
import br.com.mdsdev.spring.security.client.repository.PasswordResetTokenRepository;
import br.com.mdsdev.spring.security.client.repository.UserRepository;
import br.com.mdsdev.spring.security.client.constants.RegistrationConstants;
import br.com.mdsdev.spring.security.client.entity.User;
import br.com.mdsdev.spring.security.client.entity.VerificationToken;
import br.com.mdsdev.spring.security.client.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(UserModel userModel) {
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
        if (verificationToken == null) return RegistrationConstants.INVALID_TOKEN;

        final User user = verificationToken.getUser();
        final Calendar calendar = Calendar.getInstance();

        final long expirationTimeToken = verificationToken.getExpirationTime().getTime();
        final long currentMillis = calendar.getTimeInMillis();

        if (expirationTimeToken - currentMillis <= 0) {
            verificationTokenRepository.delete(verificationToken);
            return RegistrationConstants.EXPIRED_TOKEN;
        }

        user.setEnabled(true);
        userRepository.save(user);

        return "User verified successfully!";
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        final VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createPasswordResetToken(User user, String token) {
        final PasswordResetToken passwordResetToken = new PasswordResetToken(user, token);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if (passwordResetToken == null) return RegistrationConstants.INVALID_TOKEN;

        final Calendar calendar = Calendar.getInstance();

        final long expirationTimeToken = passwordResetToken.getExpirationTime().getTime();
        final long currentMillis = calendar.getTimeInMillis();

        if (expirationTimeToken - currentMillis <= 0) {
            passwordResetTokenRepository.delete(passwordResetToken);
            return RegistrationConstants.EXPIRED_TOKEN;
        }

        return RegistrationConstants.VALID;
    }

    @Override
    public Optional<User> getByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public boolean isValidOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

}
