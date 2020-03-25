package com.converage.entity.shop;

import lombok.Data;

@Data
public class GoodsAttr {
    private String title;
    private String content;

    public GoodsAttr() {
    }

    public GoodsAttr(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
