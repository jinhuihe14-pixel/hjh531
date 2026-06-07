package com.fresh.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.order.entity.OrderLog;
import com.fresh.order.mapper.OrderLogMapper;
import com.fresh.order.service.OrderLogService;
import org.springframework.stereotype.Service;

@Service
public class OrderLogServiceImpl extends ServiceImpl<OrderLogMapper, OrderLog> implements OrderLogService {

}
