package com.fresh.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.common.context.UserContext;
import com.fresh.common.exception.BusinessException;
import com.fresh.user.dto.AddressAddDTO;
import com.fresh.user.dto.AddressUpdateDTO;
import com.fresh.user.entity.UserAddress;
import com.fresh.user.mapper.UserAddressMapper;
import com.fresh.user.service.UserAddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAddress(AddressAddDTO addDTO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        UserAddress address = new UserAddress();
        BeanUtil.copyProperties(addDTO, address);
        address.setUserId(userId);
        if (addDTO.getIsDefault() != null && addDTO.getIsDefault() == 1) {
            LambdaUpdateWrapper<UserAddress> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(UserAddress::getUserId, userId);
            wrapper.eq(UserAddress::getIsDefault, 1);
            wrapper.set(UserAddress::getIsDefault, 0);
            update(wrapper);
        }
        save(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(AddressUpdateDTO updateDTO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        UserAddress existAddress = getById(updateDTO.getId());
        if (existAddress == null) {
            throw new BusinessException("地址不存在");
        }
        if (!existAddress.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该地址");
        }
        if (updateDTO.getIsDefault() != null && updateDTO.getIsDefault() == 1) {
            LambdaUpdateWrapper<UserAddress> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(UserAddress::getUserId, userId);
            wrapper.eq(UserAddress::getIsDefault, 1);
            wrapper.set(UserAddress::getIsDefault, 0);
            update(wrapper);
        }
        UserAddress address = new UserAddress();
        BeanUtil.copyProperties(updateDTO, address);
        updateById(address);
    }

    @Override
    public void deleteAddress(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        UserAddress existAddress = getById(id);
        if (existAddress == null) {
            throw new BusinessException("地址不存在");
        }
        if (!existAddress.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该地址");
        }
        removeById(id);
    }

    @Override
    public List<UserAddress> listAddress() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        LambdaQueryWrapper<UserAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAddress::getUserId, userId);
        wrapper.orderByDesc(UserAddress::getIsDefault);
        wrapper.orderByDesc(UserAddress::getCreateTime);
        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        UserAddress existAddress = getById(id);
        if (existAddress == null) {
            throw new BusinessException("地址不存在");
        }
        if (!existAddress.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该地址");
        }
        LambdaUpdateWrapper<UserAddress> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserAddress::getUserId, userId);
        wrapper.eq(UserAddress::getIsDefault, 1);
        wrapper.set(UserAddress::getIsDefault, 0);
        update(wrapper);
        UserAddress address = new UserAddress();
        address.setId(id);
        address.setIsDefault(1);
        updateById(address);
    }

    @Override
    public UserAddress getDefaultAddress() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        LambdaQueryWrapper<UserAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAddress::getUserId, userId);
        wrapper.eq(UserAddress::getIsDefault, 1);
        return getOne(wrapper);
    }
}
