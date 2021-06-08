package com.bookfinderclient.entity;

import lombok.Data;

@Data
public class Book {

    private int id;
    private Author author;
    private String title;
    private String imageLink;
    private String smallImageLink;


    @Data
    public static class Author {

        private int id;
        private String name;

    }

}
