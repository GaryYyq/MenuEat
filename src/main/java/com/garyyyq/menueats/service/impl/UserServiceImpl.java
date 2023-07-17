package com.garyyyq.menueats.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.garyyyq.menueats.entity.User;
import com.garyyyq.menueats.mapper.UserMapper;
import com.garyyyq.menueats.service.UserService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JavaMailSender mailSender;

    public UserServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("n10699155@qut.edu.au");
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(text);

        this.mailSender.send(simpleMailMessage);
    }
}

