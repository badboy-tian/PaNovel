package com.tian.panovel.beans;

import pl.droidsonroids.jspoon.annotation.Selector;

public class Chapter {
    @Selector("title")
    public String title;
    @Selector(value = "div.Readarea", attr = "html")
    public String content;

    @Selector(value = "p.Readpage > a#pb_prev", attr = "href")
    public String preUrl;

    @Selector(value = "p.Readpage > a#pb_next", attr = "href")
    public String nextUrl;

    @Selector(value = "p.Readpage > a#pb_mulu", attr = "href")
    public String listUrl;

    @Override
    public String toString() {
        return "Chapter{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", preUrl='" + preUrl + '\'' +
                ", nextUrl='" + nextUrl + '\'' +
                ", listUrl='" + listUrl + '\'' +
                '}';
    }
}
