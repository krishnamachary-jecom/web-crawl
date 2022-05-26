package com.dbs.web.crawler.controller;

import com.dbs.web.crawler.model.WebCrawlDetails;
import com.dbs.web.crawler.service.WebCrawlService;
import com.dbs.web.crawler.util.ApiUtil;
import com.dbs.web.crawler.vo.ApiResponse;
import com.dbs.web.crawler.vo.Crawl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class to perform crawl URL's for any given valid URL and do full text search among
 * crawled data.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/webcrawl")
public class WebCrawlerController {
  private final WebCrawlService webCrawlService;

  /**
   * Method to crawl the linked urls for any given valid URL
   *
   * @param crawl holds list of urls to crawl
   * @return list of urls crawled
   */
  @ResponseStatus(HttpStatus.OK)
  @PutMapping
  public ApiResponse<List<String>> webcrawlURLs(@Validated @RequestBody Crawl crawl) {
    List<String> crawledUrls = webCrawlService.webCrawl(crawl.getWebUrls());
    return ApiUtil.createSuccessResponse("Web crawl done successfully", crawledUrls);
  }

  /**
   * Method to fetch urls matching the text
   *
   * @param anySearchText search parameter to search from crawled datasource
   * @param pageable pageable object to hold pagination
   * @return list of crawl data matching search parameter
   */
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiResponse<List<WebCrawlDetails>> getUrls(
      @RequestParam String anySearchText, Pageable pageable) {
    return ApiUtil.createSuccessResponse(
        "Web crawl fetched successfully",
        webCrawlService.getUrls(anySearchText, pageable).getContent());
  }
}
