package com.tian.panovel.beans;

import com.i7play.supertian.beans.BaseBean;

import java.util.ArrayList;

import pl.droidsonroids.jspoon.annotation.Selector;


@Selector(".post")
public class BklistItem{
    @Selector(".hot_sale")
    public ArrayList<Item> items;

    public static class Item extends BaseBean{
        @Selector(value = "img.lazy", attr = "data-original")
        public String img;
        @Selector("p.title")
        public String title;
        @Selector("p.author")
        public String author;
        @Selector("p.review")
        public String review;
        @Selector(value = "a", attr = "href")
        public String url;

        @Override
        public String toString() {
            return "Item{" +
                    "img='" + img + '\'' +
                    ", title='" + title + '\'' +
                    ", author='" + author + '\'' +
                    ", review='" + review + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "BklistItem{" +
                "items=" + items +
                '}';
    }
}