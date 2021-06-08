package com.bookfinderclient.service;

import com.bookfinderclient.entity.Book;
import com.bookfinderclient.exception.FindBookServerCallException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

@Service
@Slf4j
public class BookServiceImpl implements BookService {

    @Value("${bookfinder.url}")
    private String serverUrl;
    @Value("${bookfinder.host}")
    private String host;
    @Value("${bookfinder.port}")
    private int serverPort;
    @Value("${bookfinder.username}")
    private String username;
    @Value("${bookfinder.password}")
    private String password;

    @Override
    public List<Book> seach(String keyword, SearchType searchType, Integer page) {
        CloseableHttpClient httpClient = prepareHttpClient();
        try {
            HttpGet request = new HttpGet("http://" + host + serverUrl);
            URI uri = new URIBuilder(request.getURI())
                    .setPort(serverPort)
                    .addParameter("term", keyword)
                    .addParameter("type", searchType == null ? null : searchType.name().toLowerCase())
                    .addParameter("page", page == null ? null : String.valueOf(page))
                    .build();
            request.setURI(uri);
            CloseableHttpResponse response = httpClient.execute(request);
            List<Book> books = deserilize(response.getEntity().getContent());
            return books;
        } catch (Throwable e) {
            log.error(e.getMessage());
            throw new FindBookServerCallException("Something wrong happened when calling server");
        }
    }

    private CloseableHttpClient prepareHttpClient() {
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials
                = new UsernamePasswordCredentials(username, password);
        provider.setCredentials(AuthScope.ANY, credentials);
        return HttpClients.custom()
                .setDefaultCredentialsProvider(provider)
                .build();
    }

    private List<Book> deserilize(InputStream inputStream) throws IOException {
        return new ObjectMapper().readValue(inputStream, new TypeReference<List<Book>>(){});
    }
}
