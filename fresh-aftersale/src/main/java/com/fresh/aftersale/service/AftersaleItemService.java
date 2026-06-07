package com.fresh.aftersale.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.aftersale.entity.AftersaleItem;

import java.util.List;

public interface AftersaleItemService extends IService<AftersaleItem> {

    List<AftersaleItem> getByAftersaleId(Long aftersaleId);

}
