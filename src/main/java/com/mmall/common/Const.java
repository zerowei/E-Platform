package com.mmall.common;

import java.util.HashSet;
import java.util.Set;

public class Const {
    public static final String USER = "currentuser";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";
    public static final String PREFIX = "token_";
    public static final Integer SUPERUSER = 1;
    public static final Integer CHECKED = 1;
    public static final Integer UNCHECKED = 0;
    public static final String OVERLIMIT = "overLimit";
    public static final String UnderLIMIT = "underLimit";
    public static final Set<String> ORDERSET = new HashSet<String>(){{add("price_asc");add("price_desc");}};

    public enum SaleStatus{
        ON_SALE(0, "ON_SALE"),
        OFF_SALE(1, "OF_SALE");

        private int code;
        private String name;

        SaleStatus(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public interface expireTime {
        int sessionExpireTime = 60 * 30;
    }

    public enum OrderStatus{
        CANCEL(0, "订单已取消"),
        WAIT_BUYER_PAY(10, "等待买家支付"),
        PAID(30, "订单已支付"),
        SHIPPED(40, "订单已发货"),
        ORDER_SUCCESS(50, "订单已完成"),
        ORDER_CLOSED(60, "订单已关闭");

        private int code;
        private String name;

        OrderStatus(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public static OrderStatus getNameByCode(int code) {
            for (OrderStatus orderStatus : values()) {
                if (orderStatus.getCode() == code) return orderStatus;
            }
            throw new RuntimeException("没有此状态码对应的描述");
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public interface TradeStatus{
        String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_SUCCESS = "TRADE_SUCCESS";
        String SUCCESS = "success";
        String FAIL = "fail";
    }

    public enum OrderPlatform{
        ALIPAY(1, "支付宝");

        private int code;
        private String name;

        OrderPlatform(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public enum PaymentType{
        ONLINE(1, "在线支付");

        private int code;
        private String name;

        PaymentType(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public static PaymentType getNameByCode(int code) {
            for (PaymentType paymentType : values()) {
                if (paymentType.getCode() == code) return paymentType;
            }
            throw new RuntimeException("没有此状态码对应的描述");
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
