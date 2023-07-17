package com.garyyyq.menueats.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.garyyyq.menueats.entity.User;

public interface UserService extends IService<User> {
    void sendEmail(String to, String subject, String message);
}
