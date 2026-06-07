package com.fresh.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.auth.dto.LoginDTO;
import com.fresh.auth.dto.RegisterDTO;
import com.fresh.auth.entity.SysUser;
import com.fresh.auth.vo.LoginVO;

public interface SysUserService extends IService<SysUser> {

    LoginVO login(LoginDTO loginDTO);

    void register(RegisterDTO registerDTO);

    void logout(String token);

    LoginVO refreshToken(String refreshToken);

    SysUser getByUsername(String username);
}
