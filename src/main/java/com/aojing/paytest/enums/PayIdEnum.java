package com.aojing.paytest.enums;

/**
 * @author gexiao
 * @date 2018/11/29 10:42
 */
public enum PayIdEnum {
    ALIPAY(1, "支付宝"),
    WX(2, "微信"),
    UNION(3, "银联"),
    ;


    PayIdEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }

    private String value;
    private int code;

    public String getValue() {
        return value;
    }

    public int getCode() {
        return code;
    }


}
