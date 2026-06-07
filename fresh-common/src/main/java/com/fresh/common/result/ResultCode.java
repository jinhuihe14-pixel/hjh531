package com.fresh.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    SERVER_ERROR(500, "服务器内部错误"),
    STOCK_NOT_ENOUGH(1001, "库存不足"),
    ORDER_NOT_EXIST(1002, "订单不存在"),
    USER_NOT_EXIST(1003, "用户不存在"),
    USER_EXIST(1004, "用户已存在"),
    PRODUCT_NOT_EXIST(1005, "商品不存在"),
    LOGIN_ERROR(1006, "用户名或密码错误"),
    TOKEN_EXPIRED(1007, "token已过期"),
    TOKEN_INVALID(1008, "token无效");

    private final Integer code;
    private final String msg;

}
