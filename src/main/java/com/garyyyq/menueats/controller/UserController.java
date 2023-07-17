package com.garyyyq.menueats.controller;

import com.garyyyq.menueats.resource.Email;
import com.garyyyq.menueats.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sendEmail")
    public ResponseEntity sendEmail(@RequestBody Email email) {
        log.info("It is working");
        this.userService.sendEmail(email.getTo(), email.getSubject(), email.getText());
        return ResponseEntity.ok("Success");
    }

}
