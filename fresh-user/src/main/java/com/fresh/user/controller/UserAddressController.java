package com.fresh.user.controller;

import com.fresh.common.result.R;
import com.fresh.user.dto.AddressAddDTO;
import com.fresh.user.dto.AddressUpdateDTO;
import com.fresh.user.entity.UserAddress;
import com.fresh.user.service.UserAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户地址管理", description = "地址的增删改查、设为默认")
@RestController
@RequestMapping("/user/address")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    @Operation(summary = "新增地址")
    @PostMapping("/add")
    public R<Void> addAddress(@Valid @RequestBody AddressAddDTO addDTO) {
        userAddressService.addAddress(addDTO);
        return R.success("新增成功", null);
    }

    @Operation(summary = "修改地址")
    @PutMapping("/update")
    public R<Void> updateAddress(@Valid @RequestBody AddressUpdateDTO updateDTO) {
        userAddressService.updateAddress(updateDTO);
        return R.success("修改成功", null);
    }

    @Operation(summary = "删除地址")
    @DeleteMapping("/delete/{id}")
    public R<Void> deleteAddress(@Parameter(description = "地址ID") @PathVariable Long id) {
        userAddressService.deleteAddress(id);
        return R.success("删除成功", null);
    }

    @Operation(summary = "地址列表")
    @GetMapping("/list")
    public R<List<UserAddress>> listAddress() {
        List<UserAddress> list = userAddressService.listAddress();
        return R.success(list);
    }

    @Operation(summary = "设为默认地址")
    @PutMapping("/default/{id}")
    public R<Void> setDefault(@Parameter(description = "地址ID") @PathVariable Long id) {
        userAddressService.setDefault(id);
        return R.success("设置成功", null);
    }

    @Operation(summary = "获取默认地址")
    @GetMapping("/default")
    public R<UserAddress> getDefaultAddress() {
        UserAddress address = userAddressService.getDefaultAddress();
        return R.success(address);
    }

}
