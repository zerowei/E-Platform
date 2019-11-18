package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

public class CartListVO {

    private List<CartVO> cartListVO;

    private BigDecimal totalPrice;

    private boolean allChecked;

    private String imagePrefix;

    public List<CartVO> getCartListVO() {
        return cartListVO;
    }

    public void setCartListVO(List<CartVO> cartListVO) {
        this.cartListVO = cartListVO;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isAllChecked() {
        return allChecked;
    }

    public void setAllChecked(boolean allChecked) {
        this.allChecked = allChecked;
    }

    public String getImagePrefix() {
        return imagePrefix;
    }

    public void setImagePrefix(String imagePrefix) {
        this.imagePrefix = imagePrefix;
    }
}
