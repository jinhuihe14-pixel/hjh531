package com.fresh.auth.controller;

import com.fresh.auth.dto.LoginDTO;
import com.fresh.auth.dto.RegisterDTO;
import com.fresh.auth.service.SysUserService;
import com.fresh.auth.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "认证管理", description = "登录、注册、登出、刷新token")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SysUserService sysUserService;

    public AuthController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = sysUserService.login(loginDTO);
        return success(loginVO);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Map<String, Object> register(@Valid @RequestBody RegisterDTO registerDTO) {
        sysUserService.register(registerDTO);
        return success("注册成功");
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Map<String, Object> logout(
            @Parameter(description = "访问令牌") @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        sysUserService.logout(token);
        return success("登出成功");
    }

    @Operation(summary = "刷新令牌")
    @PostMapping("/refresh")
    public Map<String, Object> refreshToken(
            @Parameter(description = "刷新令牌") @RequestHeader("Refresh-Token") String refreshToken) {
        LoginVO loginVO = sysUserService.refreshToken(refreshToken);
        return success(loginVO);
    }

    private Map<String, Object> success(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        return result;
    }
}
