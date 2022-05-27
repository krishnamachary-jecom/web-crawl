package com.dbs.web.crawler.service;

import com.dbs.web.crawler.model.WebCrawlDetails;
import com.dbs.web.crawler.repository.WebCrawlRepository;
import com.dbs.web.crawler.vo.SSLResolver;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.altindag.ssl.SSLFactory;
import nl.altindag.ssl.util.CertificateUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.net.ssl.HttpsURLConnection;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class WebCrawlServiceImpl implements WebCrawlService {

  private final WebCrawlRepository webCrawlRepository;

  /**
   * Method crawl from world wide web and associated internal and external urls
   * SSLResolver to be provided the url path to load urls
   * @param webUrls list of web urls
   * @return list of urls crawled
   */
  @Override
  @SSLResolver(urlPath = "[0]")
  public List<String> webCrawl(List<String> webUrls) {
    List<WebCrawlDetails> finalCrawlDetails =
        webUrls.stream()
            .map(
                url -> {
                  if (log.isInfoEnabled()) {
                    log.info("Crawling the URL: {}", url);
                  }
                  String parentId = UUID.randomUUID().toString();
                  Document doc =
                      Try.of(() -> Jsoup.connect(url).get())
                          .getOrElseThrow(
                              e ->
                                  new ResponseStatusException(
                                      HttpStatus.BAD_REQUEST,
                                      String.format("Invalid Url %s", url)));
                  Optional<Element> keywordElem =
                      doc.select("meta[name=keywords]").stream()
                          .filter(element -> ObjectUtils.isNotEmpty(element))
                          .findFirst();
                  String keywords = "";
                  if (keywordElem.isPresent()) {
                    if (log.isInfoEnabled()) {
                      log.info("Meta keyword : {}", keywords);
                    }
                    keywords = keywordElem.get().attr("content");
                  }

                  String description = "";
                  Elements descElem = doc.select("meta[name=description]");
                  if (CollectionUtils.isNotEmpty(descElem)) {
                    description = descElem.get(0).attr("content");
                    if (log.isInfoEnabled()) {
                      log.info("Meta description: {}", description);
                    }
                  }
                  Elements links = doc.select("a[href]");
                  List<WebCrawlDetails> linkedUrls =
                      links.stream()
                          .distinct()
                          .map(
                              link ->
                                  WebCrawlDetails.builder()
                                      .id(UUID.randomUUID().toString())
                                      .url(link.attr("href"))
                                      .keyword(Collections.singletonList(link.text()))
                                      .parentId(parentId)
                                      .build())
                          .collect(Collectors.toList());

                  linkedUrls.add(
                      WebCrawlDetails.builder()
                          .id(parentId)
                          .url(url)
                          .title(doc.title())
                          .keyword(Arrays.asList(StringUtils.split(keywords, ",")))
                          .description(description)
                          .linkedUrls(
                              linkedUrls.stream()
                                  .map(webCrawlDetail -> webCrawlDetail.getId())
                                  .collect(Collectors.toList()))
                          .build());
                  return linkedUrls;
                })
            .flatMap(List::stream)
            .collect(Collectors.toList());
    webCrawlRepository.saveAll(finalCrawlDetails);
    return webUrls;
  }

  /**
   * Method to fetch urls matching the text
   *
   * @param anySearchText search parameter to search from crawled datasource
   * @param pageable pageable object to hold pagination
   * @return Page of crawl data matching search parameter
   */
  @Override
  public Page<WebCrawlDetails> getUrls(String anySearchText, Pageable pageable) {
    return webCrawlRepository.findByCustomQuery(anySearchText, pageable);
  }
}
