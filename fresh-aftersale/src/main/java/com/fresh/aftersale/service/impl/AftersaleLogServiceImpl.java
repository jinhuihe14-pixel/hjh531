package com.fresh.aftersale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.aftersale.entity.AftersaleLog;
import com.fresh.aftersale.mapper.AftersaleLogMapper;
import com.fresh.aftersale.service.AftersaleLogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AftersaleLogServiceImpl extends ServiceImpl<AftersaleLogMapper, AftersaleLog> implements AftersaleLogService {

    @Override
    public void addLog(Long aftersaleId, Integer operateType, String operateDesc, Long operatorId, String operatorName, String remark) {
        AftersaleLog log = new AftersaleLog();
        log.setAftersaleId(aftersaleId);
        log.setOperateType(operateType);
        log.setOperateDesc(operateDesc);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setRemark(remark);
        save(log);
    }

    @Override
    public List<AftersaleLog> getByAftersaleId(Long aftersaleId) {
        LambdaQueryWrapper<AftersaleLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AftersaleLog::getAftersaleId, aftersaleId);
        wrapper.orderByDesc(AftersaleLog::getCreateTime);
        return list(wrapper);
    }
}
