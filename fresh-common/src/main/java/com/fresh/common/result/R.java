package com.fresh.common.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class R<T> implements Serializable {

    private Integer code;
    private String msg;
    private T data;

    public static <T> R<T> success() {
        R<T> r = new R<>();
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMsg(ResultCode.SUCCESS.getMsg());
        return r;
    }

    public static <T> R<T> success(T data) {
        R<T> r = new R<>();
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMsg(ResultCode.SUCCESS.getMsg());
        r.setData(data);
        return r;
    }

    public static <T> R<T> success(String msg, T data) {
        R<T> r = new R<>();
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    public static <T> R<T> fail() {
        R<T> r = new R<>();
        r.setCode(ResultCode.FAIL.getCode());
        r.setMsg(ResultCode.FAIL.getMsg());
        return r;
    }

    public static <T> R<T> fail(String msg) {
        R<T> r = new R<>();
        r.setCode(ResultCode.FAIL.getCode());
        r.setMsg(msg);
        return r;
    }

    public static <T> R<T> fail(Integer code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    public static <T> R<T> fail(ResultCode resultCode) {
        R<T> r = new R<>();
        r.setCode(resultCode.getCode());
        r.setMsg(resultCode.getMsg());
        return r;
    }

}
