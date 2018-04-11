package com.tian.panovel.beans;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.jspoon.annotation.Selector;

public class BookDetail {
    @Selector("div.synopsisArea_detail")
    public Book book;
    @Selector(value = "div.review", attr = "html")
    public String detail;
    @Selector(value = "div.directoryArea > p")
    public ArrayList<Zhang> items;

    @Override
    public String toString() {
        return "BookDetail{" +
                "book=" + book +
                ", detail='" + detail + '\'' +
                ", items=" + items +
                '}';
    }

    public static class Zhang{
        @Selector("p > a")
        public String name;
        @Selector(value = "p > a", attr = "href")
        public String url;

        @Override
        public String toString() {
            return "Zhang{" +
                    "name='" + name + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    public static class Book {
        @Selector(value = "img", attr = "src")
        public String img;
        @Selector("p.sort")
        public String type;
        @Selector(value = "p")
        public ArrayList<String> state;

        @Selector(value = "p > a", attr = "href")
        public String newpostUrl;
        @Selector(value = "p > a")
        public String newpostText;

        @Override
        public String toString() {
            return "Book{" +
                    "img='" + img + '\'' +
                    ", type='" + type + '\'' +
                    ", state=" + state +
                    ", newpostUrl='" + newpostUrl + '\'' +
                    ", newpostText='" + newpostText + '\'' +
                    '}';
        }
    }
}
