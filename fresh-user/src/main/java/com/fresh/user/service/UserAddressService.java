package com.fresh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.user.dto.AddressAddDTO;
import com.fresh.user.dto.AddressUpdateDTO;
import com.fresh.user.entity.UserAddress;

import java.util.List;

public interface UserAddressService extends IService<UserAddress> {

    void addAddress(AddressAddDTO addDTO);

    void updateAddress(AddressUpdateDTO updateDTO);

    void deleteAddress(Long id);

    List<UserAddress> listAddress();

    void setDefault(Long id);

    UserAddress getDefaultAddress();

}
