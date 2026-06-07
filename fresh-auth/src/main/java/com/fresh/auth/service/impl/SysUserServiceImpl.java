package com.fresh.auth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.auth.dto.LoginDTO;
import com.fresh.auth.dto.RegisterDTO;
import com.fresh.auth.entity.SysUser;
import com.fresh.auth.mapper.SysUserMapper;
import com.fresh.auth.service.SysUserService;
import com.fresh.auth.util.JwtUtil;
import com.fresh.auth.vo.LoginVO;
import com.fresh.auth.vo.UserInfoVO;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final JwtUtil jwtUtil;

    public SysUserServiceImpl(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        SysUser user = getByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        String accessToken = jwtUtil.generateToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        UserInfoVO userInfoVO = BeanUtil.copyProperties(user, UserInfoVO.class);
        return new LoginVO(accessToken, refreshToken, jwtUtil.getExpire(), userInfoVO);
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        SysUser existUser = getByUsername(registerDTO.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        SysUser user = new SysUser();
        BeanUtil.copyProperties(registerDTO, user);
        user.setPassword(BCrypt.hashpw(registerDTO.getPassword()));
        if (user.getNickname() == null || user.getNickname().isEmpty()) {
            user.setNickname(user.getUsername());
        }
        user.setStatus(1);
        user.setRole("USER");
        save(user);
    }

    @Override
    public void logout(String token) {
        jwtUtil.removeToken(token);
    }

    @Override
    public LoginVO refreshToken(String refreshToken) {
        Long userId = jwtUtil.getUserIdFromRefreshToken(refreshToken);
        if (userId == null) {
            throw new RuntimeException("刷新令牌无效或已过期");
        }
        jwtUtil.removeRefreshToken(refreshToken);
        SysUser user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        String newAccessToken = jwtUtil.generateToken(user.getId(), user.getUsername());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId());
        UserInfoVO userInfoVO = BeanUtil.copyProperties(user, UserInfoVO.class);
        return new LoginVO(newAccessToken, newRefreshToken, jwtUtil.getExpire(), userInfoVO);
    }

    @Override
    public SysUser getByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return getOne(wrapper);
    }
}
