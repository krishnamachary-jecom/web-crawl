package com.dbs.web.crawler.service;

import com.dbs.web.crawler.model.WebCrawlDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WebCrawlService {

  List<String> webCrawl(List<String> webUrls);

  Page<WebCrawlDetails> getUrls(String anySearchText, Pageable pageable);
}
