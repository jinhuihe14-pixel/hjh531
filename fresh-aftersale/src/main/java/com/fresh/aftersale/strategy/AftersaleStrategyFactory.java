package com.fresh.aftersale.strategy;

import com.fresh.aftersale.enums.AftersaleTypeEnum;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AftersaleStrategyFactory {

    @Resource
    private List<AftersaleStrategy> strategies;

    private final Map<Integer, AftersaleStrategy> strategyMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (AftersaleStrategy strategy : strategies) {
            strategyMap.put(strategy.getType(), strategy);
        }
    }

    public AftersaleStrategy getStrategy(Integer type) {
        AftersaleStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new RuntimeException("不支持的售后类型：" + type);
        }
        return strategy;
    }

    public AftersaleStrategy getStrategy(AftersaleTypeEnum typeEnum) {
        return getStrategy(typeEnum.getCode());
    }
}
