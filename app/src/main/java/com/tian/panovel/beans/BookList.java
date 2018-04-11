package com.tian.panovel.beans;

import java.util.ArrayList;

import pl.droidsonroids.jspoon.annotation.Selector;

public class BookList {
    @Selector("span.title")
    public String title;

    @Selector(value = "div.directoryArea > p")
    public ArrayList<BookDetail.Zhang> items;

    @Override
    public String toString() {
        return "BookList{" +
                "title='" + title + '\'' +
                ", items=" + items +
                '}';
    }
}
