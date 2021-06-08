package com.bookfinderclient.controller;

import com.bookfinderclient.entity.Book;
import com.bookfinderclient.exception.DisplayCommandException;
import com.bookfinderclient.service.BookService;
import com.bookfinderclient.service.SearchType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.regex.Pattern;

@Controller
@Slf4j
public class BookCommandLineController {

    @Value("${bookfinder.host}")
    private String host;

    private static final String HELP_PATTERN = "--help";
    private static final String SEARCH_PATTERN = "(--search|-s)(\\s+\\w+){0,2}";
    private static final String SORT_PATTERN = "--sort\\s+\\w+";
    private static final String PAGE_PATTERN = "-p\\s+\\d+";
    private static final String HOST_PATTERN = "-h|--host";
    private static final String ERROR_MESSAGE = "Invalid command";
    private static final String HELP_TEXT = "-s, --search TERMS Search the Goodreads' API and display the results on screen.\n"
            + "--sort FIELD where field is one of \"author\" or \"title\"\n"
            + "-p NUMBER display the _NUMBER_ page of results\n"
            + "-h, --host display HOSTNAME the hostname or ip address\n";
    private static final String SORT_BY_AUTHOR = "author";
    private static final String SORT_BY_TITLE = "title";

    @Autowired
    private BookService bookService;

    public void readAndDisplay() {
        Scanner scanner = new Scanner(System.in);
        List<Book> books = null;
        int page = 1;
        String term = null;
        SearchType searchType = null;

        while (scanner.hasNextLine()) {
            String currLine = scanner.nextLine();
            try {
                if (Pattern.compile(HELP_PATTERN).matcher(currLine.trim()).matches()) {
                    System.out.println(HELP_TEXT);
                } else if (Pattern.compile(SEARCH_PATTERN).matcher(currLine.trim()).matches()) {
                    String[] segs = currLine.split("\\s+");
                    if (segs.length < 2) {
                        throw new DisplayCommandException("Missing search term.");
                    }
                    term = segs[1];
                    if (segs.length > 2) {
                        try {
                            searchType = SearchType.valueOf(segs[2].toUpperCase());
                        } catch (Exception e) {
                            searchType = null;
                        }
                    }
                    books = bookService.seach(term, searchType, page);
                    displayBooks(books, page);
                } else if (Pattern.compile(SORT_PATTERN).matcher(currLine.trim()).matches()) {
                    String[] segs = currLine.split("\\s+");
                    if (segs.length < 1) {
                        throw new DisplayCommandException("Missing sort term.");
                    }
                    if (!SORT_BY_AUTHOR.equals(segs[1]) && !SORT_BY_TITLE.equals(segs[1])) {
                        throw new DisplayCommandException("Invalid search term. Should be author or title.");
                    }
                    if (SORT_BY_AUTHOR.equals(segs[1])) {
                        Collections.sort(books, Comparator.comparing(v -> v.getAuthor().getName()));
                    } else if (SORT_BY_TITLE.equals(segs[1])) {
                        Collections.sort(books, Comparator.comparing(v -> v.getTitle()));
                    }
                    displayBooks(books, page);
                } else if (Pattern.compile(PAGE_PATTERN).matcher(currLine.trim()).matches()) {
                    if (term == null) {
                        throw new DisplayCommandException("Please do search first.");
                    }
                    String[] segs = currLine.split("\\s+");
                    page = Integer.valueOf(segs[1]);
                    books = bookService.seach(term, searchType, page);
                    displayBooks(books, page);
                } else if (Pattern.compile(HOST_PATTERN).matcher(currLine.trim()).matches()) {
                    System.out.println("HOST: " + host);
                } else {
                    System.out.println(ERROR_MESSAGE);
                }
            } catch (DisplayCommandException e) {
                System.out.println(e.getMessage());
            } catch (Throwable e) {
                log.error(e.getMessage());
                System.out.println("Something unexpected happened. Please try again.");
            }

        }
    }

    private void displayBooks(List<Book> books, int page) {
        System.out.println("Page " + page);
        System.out.println("Title          " + "Author         " + "Image link");
        for (Book book : books) {
            System.out.println(book.getAuthor().getName()
                    + "          " + book.getTitle()
                    + "          " + book.getImageLink());
        }
    }
}
