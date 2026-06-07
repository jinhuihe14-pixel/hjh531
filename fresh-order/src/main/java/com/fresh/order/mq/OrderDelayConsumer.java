package com.fresh.order.mq;

import com.alibaba.fastjson2.JSON;
import com.fresh.order.service.OrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Configuration
public class OrderDelayConsumer {

    @Resource
    private OrderService orderService;

    @Bean
    public Consumer<Map<String, Object>> orderDelayInput() {
        return message -> {
            try {
                log.info("收到订单延迟消息: {}", JSON.toJSONString(message));
                String orderNo = (String) message.get("orderNo");
                if (orderNo != null) {
                    orderService.cancelTimeoutOrder(orderNo);
                }
            } catch (Exception e) {
                log.error("处理订单延迟消息失败", e);
            }
        };
    }

}
