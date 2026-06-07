package com.fresh.aftersale.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.aftersale.entity.AftersaleLog;

import java.util.List;

public interface AftersaleLogService extends IService<AftersaleLog> {

    void addLog(Long aftersaleId, Integer operateType, String operateDesc, Long operatorId, String operatorName, String remark);

    List<AftersaleLog> getByAftersaleId(Long aftersaleId);

}
