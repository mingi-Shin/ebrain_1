package com.study.model;

public class Category {
    private Long categorySeq;
    private String categoryName;
    private String categoryDescribe;

    public Category(){};

    public Long getCategorySeq() {
        return categorySeq;
    }

    public void setCategorySeq(Long categorySeq) {
        this.categorySeq = categorySeq;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDescribe() {
        return categoryDescribe;
    }

    public void setCategoryDescribe(String categoryDescribe) {
        this.categoryDescribe = categoryDescribe;
    }
}
