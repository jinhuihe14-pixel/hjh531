package com.fresh.order.strategy;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderSplitStrategyFactory {

    private final Map<String, OrderSplitStrategy> strategyMap = new ConcurrentHashMap<>();

    @Value("${order.split.strategy:single-first}")
    private String defaultStrategy;

    @Resource
    public OrderSplitStrategyFactory(List<OrderSplitStrategy> strategies) {
        for (OrderSplitStrategy strategy : strategies) {
            strategyMap.put(strategy.getStrategyName(), strategy);
        }
    }

    public OrderSplitStrategy getStrategy() {
        return getStrategy(defaultStrategy);
    }

    public OrderSplitStrategy getStrategy(String strategyName) {
        OrderSplitStrategy strategy = strategyMap.get(strategyName);
        if (strategy == null) {
            throw new RuntimeException("拆单策略不存在: " + strategyName);
        }
        return strategy;
    }

}
