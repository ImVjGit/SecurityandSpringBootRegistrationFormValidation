package com.SpringSecurityClient.Controller;

import com.SpringSecurityClient.Entity.User;
import com.SpringSecurityClient.Entity.VerificationToken;
import com.SpringSecurityClient.Event.RegistrationCompleteEvent;
import com.SpringSecurityClient.Model.PasswordModel;
import com.SpringSecurityClient.Model.UserModel;
import com.SpringSecurityClient.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;


@RestController
@Slf4j
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request) {
        User user = userService.registerUser(userModel);
        publisher.publishEvent(new RegistrationCompleteEvent(user,
                applicationUrl(request)
        ));
        return "User SuccessFully Register.";
    }


    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        System.out.println("verifyRegistration Emtjod and toke is : -->>>>>" +token);
        String result = userService.validateVerificationToken(token);
        if (result.equalsIgnoreCase("valid")) {
            return "User verify Successfully";
        }
        return "Bad User";
    }


    private String applicationUrl(HttpServletRequest request) {
        return "http://" +
                request.getServerName() +
                ":" +
                request.getServerPort() +
                request.getContextPath();
    }



    @GetMapping("/resendVerifyToken")
  public String resendVerificationToken(@RequestParam("token")String oldToken,
                                        HttpServletRequest request)
  {
      VerificationToken verificationToken= userService.generateNewVerificationToken(oldToken);
      User user = verificationToken.getUser();
      resendVerificationTokenMail(user,applicationUrl(request),verificationToken);
      return"Verification Link Send -: ";

  }

    private void resendVerificationTokenMail(User user, String applicationUrl,VerificationToken verificationToken) {
String url =
        applicationUrl
                + "/verifyRegistration?token="
                + verificationToken.getToken();
        //send verification  mail
        log.info("Click to Varify ur Account :{}",
                url);
    }


    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel,HttpServletRequest request)
    {
        User user = userService.findUserByEmail(passwordModel.getEmail());
        String url = "";
        if(user!=null)
        {
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user,token);
url= passwordResetTokenMail(user,applicationUrl(request),token);
        }
        return  url;


    }

    private String passwordResetTokenMail(User user, String applicationUrl, String token) {
        String url =
                applicationUrl
                        + "/savePassword?token="
                        + token;
        //send verification  mail
        log.info("Click to reset your passwrod  :{}",
                url);
        return url;

    }



    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token")String token,@RequestBody PasswordModel passwordModel) {
        String result = userService.validatePasswordRestToken(token);
        if (!result.equalsIgnoreCase("valid")) {
            return "invalid token";
        }
        Optional<User> user = userService.getUserByPasswordResetToken(token);
        if (user.isPresent()) {
            userService.changePassword(user.get(),passwordModel.getNewPassword());
            return "Password reset successfully";
        } else {
            return "Invalid Token ";
        }
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordModel passwordModel){
        User user= userService.findUserByEmail(passwordModel.getEmail());
        if(!userService.checkIfValidOldPassword(user,passwordModel.getOldPassword()))
        {
            return "Invalid old Password";
        }
        userService.changePassword(user,passwordModel.getNewPassword());
        return "Password Change Successfully ";
    }



}
