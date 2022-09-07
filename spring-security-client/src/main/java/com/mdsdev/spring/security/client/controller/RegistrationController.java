package com.mdsdev.spring.security.client.controller;

import com.mdsdev.spring.security.client.constants.RegistrationConstants;
import com.mdsdev.spring.security.client.entity.User;
import com.mdsdev.spring.security.client.entity.VerificationToken;
import com.mdsdev.spring.security.client.event.RegistrationCompleteEvent;
import com.mdsdev.spring.security.client.model.PasswordModel;
import com.mdsdev.spring.security.client.model.UserModel;
import com.mdsdev.spring.security.client.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RegistrationController {

    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request) {
        final User user = userService.register(userModel);
        publisher.publishEvent(new RegistrationCompleteEvent(user, getApplicationUrl(request)));
        return "Success";
    }

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        return userService.validateVerificationToken(token);
    }

    @GetMapping("/resendVerificationToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken,
                                          final HttpServletRequest request) {
        final VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);
        return resendVerificationTokenEmail(verificationToken, getApplicationUrl(request));
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody @Valid PasswordModel passwordModel,
                                final HttpServletRequest request) {
        String url = "";
        final User user = userService.findByEmail(passwordModel.getEmail());
        if (user != null) {
            final String token = UUID.randomUUID().toString();
            userService.createPasswordResetToken(user, token);
            url = sendPasswordResetTokenEmail(token, request);
        }
        return url;
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token,
                               @RequestBody @Valid PasswordModel passwordModel) {
        final String result = userService.validatePasswordResetToken(token);
        if (!result.equalsIgnoreCase(RegistrationConstants.VALID)) {
            return result;
        }
        Optional<User> user = userService.getByPasswordResetToken(token);
        if (user.isPresent()) {
            userService.changePassword(user.get(), passwordModel.getNewPassword());
            return "Password reset successfully!";
        } else {
            return RegistrationConstants.INVALID_TOKEN;
        }
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordModel passwordModel) {
        final User user = userService.findByEmail(passwordModel.getEmail());
        if (!userService.isValidOldPassword(user, passwordModel.getOldPassword())) {
            return "Invalid old password.";
        }
        userService.changePassword(user, passwordModel.getNewPassword());
        return "Password changed successfully!";
    }

    private String sendPasswordResetTokenEmail(String token, HttpServletRequest request) {
        final String url = getApplicationUrl(request)
                + "/savePassword?token="
                + token;
        // TODO sendEmail()
        log.info("Click the link to reset your password: {}", url);
        return url;
    }

    private String resendVerificationTokenEmail(VerificationToken verificationToken, String applicationUrl) {
        final String url = applicationUrl
                + "/verifyRegistration?token="
                + verificationToken.getToken();
        // TODO sendEmail()
        log.info("Click the link to verify your account: {}", url);
        return "A new verification link has been sent.";
    }

    private String getApplicationUrl(HttpServletRequest request) {
        return "http://"
                + request.getServerName()
                + ":"
                + request.getServerPort()
                + request.getContextPath();
    }

}
