package com.dy.comment.controller;

import com.dy.comment.annotation.RequireRole;
import com.dy.comment.dto.*;
import com.dy.comment.entity.User;
import com.dy.comment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterRequest req) {
        userService.register(req.getPhone(), req.getPassword(), req.getConfirmPassword());
        return Result.ok("注册成功");
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest req) {
        return Result.ok(userService.login(req.getPhone(), req.getPassword()));
    }

    @RequireRole
    @GetMapping("/info")
    public Result<User> info() {
        return Result.ok(userService.getCurrentUser());
    }

    @RequireRole
    @PutMapping("/password")
    public Result<String> updatePassword(@RequestBody Map<String, String> body) {
        userService.updatePassword(body.get("oldPassword"), body.get("newPassword"));
        return Result.ok("密码修改成功");
    }

    @RequireRole
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        userService.logout(token);
        return Result.ok("已退出");
    }
}
