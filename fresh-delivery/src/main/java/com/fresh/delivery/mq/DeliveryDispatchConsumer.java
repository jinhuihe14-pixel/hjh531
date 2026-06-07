package com.fresh.delivery.mq;

import com.alibaba.fastjson2.JSON;
import com.fresh.delivery.dto.DeliveryOrderCreateDTO;
import com.fresh.delivery.service.DeliveryOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Configuration
public class DeliveryDispatchConsumer {

    @Resource
    private DeliveryOrderService deliveryOrderService;

    @Bean
    public Consumer<Map<String, Object>> deliveryDispatchInput() {
        return message -> {
            try {
                log.info("收到配送派单消息: {}", JSON.toJSONString(message));

                String orderNo = (String) message.get("orderNo");
                Long orderWarehouseId = message.get("orderWarehouseId") != null
                        ? Long.valueOf(message.get("orderWarehouseId").toString()) : null;
                Long warehouseId = message.get("warehouseId") != null
                        ? Long.valueOf(message.get("warehouseId").toString()) : null;
                String receiverName = (String) message.get("receiverName");
                String receiverPhone = (String) message.get("receiverPhone");
                String receiverAddress = (String) message.get("receiverAddress");
                java.math.BigDecimal longitude = message.get("longitude") != null
                        ? new java.math.BigDecimal(message.get("longitude").toString()) : null;
                java.math.BigDecimal latitude = message.get("latitude") != null
                        ? new java.math.BigDecimal(message.get("latitude").toString()) : null;
                String remark = (String) message.get("remark");

                if (orderNo != null && warehouseId != null && longitude != null && latitude != null) {
                    DeliveryOrderCreateDTO dto = new DeliveryOrderCreateDTO();
                    dto.setOrderNo(orderNo);
                    dto.setOrderWarehouseId(orderWarehouseId);
                    dto.setWarehouseId(warehouseId);
                    dto.setReceiverName(receiverName);
                    dto.setReceiverPhone(receiverPhone);
                    dto.setReceiverAddress(receiverAddress);
                    dto.setLongitude(longitude);
                    dto.setLatitude(latitude);
                    dto.setRemark(remark);

                    String deliveryNo = deliveryOrderService.createDeliveryOrder(dto);
                    log.info("消息创建配送单成功，配送单号：{}", deliveryNo);
                }

            } catch (Exception e) {
                log.error("处理配送派单消息失败", e);
            }
        };
    }

}
