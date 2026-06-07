package com.fresh.delivery.mq;

import com.alibaba.fastjson2.JSON;
import com.fresh.delivery.service.DeliveryOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Configuration
public class DeliveryDelayConsumer {

    @Resource
    private DeliveryOrderService deliveryOrderService;

    @Bean
    public Consumer<Map<String, Object>> deliveryDelayInput() {
        return message -> {
            try {
                log.info("收到配送延迟消息: {}", JSON.toJSONString(message));

                String deliveryNo = (String) message.get("deliveryNo");
                String type = (String) message.get("type");

                if (deliveryNo != null) {
                    if ("warning".equals(type)) {
                        log.info("配送即将超时预警，配送单号：{}", deliveryNo);
                    } else if ("overtime".equals(type)) {
                        log.warn("配送已超时，配送单号：{}", deliveryNo);
                    }
                }

            } catch (Exception e) {
                log.error("处理配送延迟消息失败", e);
            }
        };
    }

}
