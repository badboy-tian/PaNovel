package com.tian.panovel.beans;

import xiaofei.library.datastorage.annotation.ObjectId;

public class BookMark {
    public String lastReadName;//上次阅读
    public String lastReadUrl;
    public String newReadName = "";//最新
    public String newReadUrl = "";//最新
    public BklistItem.Item item;

    @ObjectId
    public String getId() {
        return item.url;
    }

    public BookMark(String lastReadName, String lastReadUrl, BklistItem.Item item) {
        this.lastReadName = lastReadName;
        this.lastReadUrl = lastReadUrl;
        this.item = item;
    }

    @Override
    public String toString() {
        return "BookMark{" +
                "lastReadName='" + lastReadName + '\'' +
                ", lastReadUrl='" + lastReadUrl + '\'' +
                ", newReadName='" + newReadName + '\'' +
                ", newReadUrl='" + newReadUrl + '\'' +
                ", item=" + item +
                '}';
    }
}
