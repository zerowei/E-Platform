package com.mmall.vo;

import com.mmall.pojo.Product;
import com.mmall.utils.PropertiesUtil;

import java.math.BigDecimal;

public class ProductListVO {
    private Integer id;

    private Integer categoryId;

    private String name;

    private String subtitle;

    private String mainImage;

    private BigDecimal price;

    private Integer stock;

    private Integer status;

    private String imageAddressPrefix;

    public ProductListVO(Product product) {
        this.id = product.getId();
        this.categoryId = product.getCategoryId();
        this.name = product.getName();
        this.subtitle = product.getSubtitle();
        this.mainImage = product.getMainImage();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.status = product.getStatus();
        this.imageAddressPrefix = PropertiesUtil.getProperties("ftp.server.http.prefix");
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getImageAddressPrefix() {
        return imageAddressPrefix;
    }

    public void setImageAddressPrefix(String imageAddressPrefix) {
        this.imageAddressPrefix = imageAddressPrefix;
    }
}
