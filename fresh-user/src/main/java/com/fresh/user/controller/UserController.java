package com.fresh.user.controller;

import com.fresh.common.result.R;
import com.fresh.user.dto.UserLoginDTO;
import com.fresh.user.dto.UserRegisterDTO;
import com.fresh.user.dto.UserUpdateDTO;
import com.fresh.user.service.UserService;
import com.fresh.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理", description = "注册、登录、用户信息管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public R<Void> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        userService.register(registerDTO);
        return R.success("注册成功", null);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<UserVO> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        UserVO userVO = userService.login(loginDTO);
        return R.success(userVO);
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/info")
    public R<UserVO> getUserInfo() {
        UserVO userVO = userService.getUserInfo();
        return R.success(userVO);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/update")
    public R<Void> updateUser(@Valid @RequestBody UserUpdateDTO updateDTO) {
        userService.updateUser(updateDTO);
        return R.success("更新成功", null);
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public R<Void> updatePassword(
            @Parameter(description = "原密码") @RequestParam String oldPassword,
            @Parameter(description = "新密码") @RequestParam String newPassword) {
        userService.updatePassword(oldPassword, newPassword);
        return R.success("密码修改成功", null);
    }

}
