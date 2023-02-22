package com.SpringSecurityClient.Event.listener;

import com.SpringSecurityClient.Entity.User;
import com.SpringSecurityClient.Event.RegistrationCompleteEvent;
import com.SpringSecurityClient.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserService userService;


    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token,user);

//sendMail
        String url = event.getApplicationUrl()
                + "/verifyRegistration?token="
                + token;
        //send verification  mail
        log.info("Click to Varify ur Account :{}",
                url);
    }
}
