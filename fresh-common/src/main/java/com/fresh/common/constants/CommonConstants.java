package com.fresh.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class CommonConstants {

    public static final String TOKEN_HEADER = "Authorization";

    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String USER_ID = "userId";

    public static final Integer DEFAULT_PAGE_NUM = 1;

    public static final Integer DEFAULT_PAGE_SIZE = 10;

    public static final Integer MAX_PAGE_SIZE = 100;

    @Getter
    @AllArgsConstructor
    public enum OrderStatus {

        PENDING_PAYMENT(0, "待付款"),
        PENDING_SHIPMENT(1, "待发货"),
        SHIPPED(2, "已发货"),
        COMPLETED(3, "已完成"),
        CANCELLED(4, "已取消"),
        REFUNDING(5, "退款中"),
        REFUNDED(6, "已退款");

        private final Integer code;
        private final String desc;

    }

    @Getter
    @AllArgsConstructor
    public enum StockType {

        NORMAL(1, "普通库存"),
        ACTIVITY(2, "活动库存"),
        SECKILL(3, "秒杀库存");

        private final Integer code;
        private final String desc;

    }

    @Getter
    @AllArgsConstructor
    public enum DeliveryStatus {

        PENDING(0, "待配送"),
        DELIVERING(1, "配送中"),
        DELIVERED(2, "已送达"),
        FAILED(3, "配送失败");

        private final Integer code;
        private final String desc;

    }

    @Getter
    @AllArgsConstructor
    public enum PayStatus {

        UNPAID(0, "未支付"),
        PAID(1, "已支付"),
        REFUNDING(2, "退款中"),
        REFUNDED(3, "已退款");

        private final Integer code;
        private final String desc;

    }

    @Getter
    @AllArgsConstructor
    public enum UserStatus {

        NORMAL(0, "正常"),
        DISABLED(1, "禁用");

        private final Integer code;
        private final String desc;

    }

    @Getter
    @AllArgsConstructor
    public enum ProductStatus {

        ON_SALE(0, "上架"),
        OFF_SALE(1, "下架");

        private final Integer code;
        private final String desc;

    }

}
