package com.dy.comment.service;

import com.dy.comment.dto.LoginResponse;
import com.dy.comment.entity.User;

public interface UserService {
    void register(String phone, String password, String confirmPassword);
    LoginResponse login(String phone, String password);
    User getCurrentUser();
    void updatePassword(String oldPassword, String newPassword);
    void logout(String token);
}
