package com.bookfinderclient.service;

import com.bookfinderclient.entity.Book;

import java.util.List;

public interface BookService {

    List<Book> seach(String keyword, SearchType searchType, Integer page);
}
