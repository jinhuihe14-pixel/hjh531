package com.fresh.aftersale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.aftersale.entity.AftersaleItem;
import com.fresh.aftersale.mapper.AftersaleItemMapper;
import com.fresh.aftersale.service.AftersaleItemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AftersaleItemServiceImpl extends ServiceImpl<AftersaleItemMapper, AftersaleItem> implements AftersaleItemService {

    @Override
    public List<AftersaleItem> getByAftersaleId(Long aftersaleId) {
        LambdaQueryWrapper<AftersaleItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AftersaleItem::getAftersaleId, aftersaleId);
        return list(wrapper);
    }
}
