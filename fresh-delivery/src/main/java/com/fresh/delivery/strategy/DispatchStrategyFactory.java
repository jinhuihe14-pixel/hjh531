package com.fresh.delivery.strategy;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DispatchStrategyFactory {

    private final Map<String, DispatchStrategy> strategyMap = new ConcurrentHashMap<>();

    @Value("${delivery.dispatch.strategy:nearest}")
    private String defaultStrategy;

    @Resource
    public DispatchStrategyFactory(List<DispatchStrategy> strategies) {
        for (DispatchStrategy strategy : strategies) {
            strategyMap.put(strategy.getStrategyName(), strategy);
        }
    }

    public DispatchStrategy getStrategy() {
        return getStrategy(defaultStrategy);
    }

    public DispatchStrategy getStrategy(String strategyName) {
        DispatchStrategy strategy = strategyMap.get(strategyName);
        if (strategy == null) {
            throw new RuntimeException("派单策略不存在: " + strategyName);
        }
        return strategy;
    }

}
