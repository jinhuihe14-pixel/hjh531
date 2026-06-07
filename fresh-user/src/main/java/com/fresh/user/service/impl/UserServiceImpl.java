package com.fresh.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.common.context.UserContext;
import com.fresh.common.exception.BusinessException;
import com.fresh.user.dto.UserLoginDTO;
import com.fresh.user.dto.UserRegisterDTO;
import com.fresh.user.dto.UserUpdateDTO;
import com.fresh.user.entity.User;
import com.fresh.user.mapper.UserMapper;
import com.fresh.user.service.UserService;
import com.fresh.user.vo.UserVO;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public void register(UserRegisterDTO registerDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, registerDTO.getUsername());
        User existUser = getOne(wrapper);
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        BeanUtil.copyProperties(registerDTO, user);
        user.setPassword(BCrypt.hashpw(registerDTO.getPassword()));
        if (user.getNickname() == null || user.getNickname().isEmpty()) {
            user.setNickname(user.getUsername());
        }
        user.setStatus(1);
        user.setIntegral(0);
        user.setLevel(1);
        save(user);
    }

    @Override
    public UserVO login(UserLoginDTO loginDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = getOne(wrapper);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        return BeanUtil.copyProperties(user, UserVO.class);
    }

    @Override
    public UserVO getUserInfo() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return BeanUtil.copyProperties(user, UserVO.class);
    }

    @Override
    public void updateUser(UserUpdateDTO updateDTO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        User user = new User();
        BeanUtil.copyProperties(updateDTO, user);
        user.setId(userId);
        updateById(user);
    }

    @Override
    public void updatePassword(String oldPassword, String newPassword) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(BCrypt.hashpw(newPassword));
        updateById(user);
    }
}
