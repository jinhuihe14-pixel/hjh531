package com.fresh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.user.dto.UserLoginDTO;
import com.fresh.user.dto.UserRegisterDTO;
import com.fresh.user.dto.UserUpdateDTO;
import com.fresh.user.entity.User;
import com.fresh.user.vo.UserVO;

public interface UserService extends IService<User> {

    void register(UserRegisterDTO registerDTO);

    UserVO login(UserLoginDTO loginDTO);

    UserVO getUserInfo();

    void updateUser(UserUpdateDTO updateDTO);

    void updatePassword(String oldPassword, String newPassword);

}
