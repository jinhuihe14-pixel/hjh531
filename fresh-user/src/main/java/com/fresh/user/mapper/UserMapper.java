package com.fresh.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fresh.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
