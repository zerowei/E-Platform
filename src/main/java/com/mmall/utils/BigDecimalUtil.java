package com.mmall.utils;

import java.math.BigDecimal;

public class BigDecimalUtil {

    public static BigDecimal add(double a, double b) {
        BigDecimal a1 = new BigDecimal(Double.toString(a));
        BigDecimal b1 = new BigDecimal(Double.toString(b));
        return a1.add(b1);
    }

    public static BigDecimal subtract(double a, double b) {
        BigDecimal a1 = new BigDecimal(Double.toString(a));
        BigDecimal b1 = new BigDecimal(Double.toString(b));
        return a1.subtract(b1);
    }

    public static BigDecimal multiply(double a, double b) {
        BigDecimal a1 = new BigDecimal(Double.toString(a));
        BigDecimal b1 = new BigDecimal(Double.toString(b));
        return a1.multiply(b1);
    }

    public static BigDecimal divide(double a, double b) {
        BigDecimal a1 = new BigDecimal(Double.toString(a));
        BigDecimal b1 = new BigDecimal(Double.toString(b));
        return a1.divide(b1, 2, BigDecimal.ROUND_HALF_UP);
    }
}
