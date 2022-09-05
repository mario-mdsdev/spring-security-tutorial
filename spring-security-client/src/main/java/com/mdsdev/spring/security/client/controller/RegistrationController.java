package com.mdsdev.spring.security.client.controller;

import com.mdsdev.spring.security.client.entity.User;
import com.mdsdev.spring.security.client.event.RegistrationCompleteEvent;
import com.mdsdev.spring.security.client.model.UserModel;
import com.mdsdev.spring.security.client.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RegistrationController {

    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request) {
        final User user = userService.registerUser(userModel);
        publisher.publishEvent(new RegistrationCompleteEvent(user, getApplicationUrl(request)));
        return "Success";
    }

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        return userService.validateVerificationToken(token);
    }

    private String getApplicationUrl(HttpServletRequest request) {
        return "http://"
                + request.getServerName()
                + ":"
                + request.getServerPort()
                + request.getContextPath();
    }

}
