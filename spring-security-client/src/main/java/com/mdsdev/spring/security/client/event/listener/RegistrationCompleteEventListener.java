package com.mdsdev.spring.security.client.event.listener;

import com.mdsdev.spring.security.client.entity.User;
import com.mdsdev.spring.security.client.event.RegistrationCompleteEvent;
import com.mdsdev.spring.security.client.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    private final UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // --> Create the user's verification token with link
        final User user = event.getUser();
        final String token = UUID.randomUUID().toString();
        userService.saveVerificationToken(user, token);
        // --> Send mail to user
        final String url = event.getApplicationUrl()
                + "/verifyRegistration?token="
                + token;
        // TODO sendEmail()
        log.info("Click the link to verify your account: {}", url);
    }

}
