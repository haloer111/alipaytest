package com.aojing.paytest.pojo;

public class ProductPojoWithBLOBs extends ProductPojo {
    private String subImages;

    private String detail;

    public String getSubImages() {
        return subImages;
    }

    public void setSubImages(String subImages) {
        this.subImages = subImages == null ? null : subImages.trim();
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail == null ? null : detail.trim();
    }
}