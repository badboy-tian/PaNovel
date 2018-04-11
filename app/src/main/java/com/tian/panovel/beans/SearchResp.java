package com.tian.panovel.beans;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.jspoon.annotation.Selector;

public class SearchResp {
    @Selector("div.hot_sale")
    public ArrayList<Item> books;
    @Selector(value = "input.page_txt", attr = "value")
    public String pageInfo;

    public static class Item{
        @Selector(value = "a", attr = "href")
        public String bookId;
        @Selector("p.title")
        public String title;
        @Selector("p.author")
        public List<String> infos;

        @Override
        public String toString() {
            return "Item{" +
                    "bookId='" + bookId + '\'' +
                    ", title='" + title + '\'' +
                    ", infos=" + infos +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SearchResp{" +
                "books=" + books +
                ", pageInfo='" + pageInfo + '\'' +
                '}';
    }
}
