package com.dbs.web.crawler.service;

import com.dbs.web.crawler.model.WebCrawlDetails;
import com.dbs.web.crawler.repository.WebCrawlRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class WebCrawlServiceImpl implements WebCrawlService {

  private final WebCrawlRepository webCrawlRepository;

  /**
   * Method crawl from world wide web and associated internal and external urls
   *
   * @param webUrls list of web urls
   * @return list of urls crawled
   */
  @Override
  public List<String> webCrawl(List<String> webUrls) {
    Map<String, List<X509Certificate>> certificates = CertificateUtils.getCertificate(webUrls);
    List<Certificate> certs =
        certificates.values().stream().flatMap(List::stream).collect(Collectors.toList());
    SSLFactory sslFactory = SSLFactory.builder().withTrustMaterial(certs).build();
    HttpsURLConnection.setDefaultSSLSocketFactory(sslFactory.getSslSocketFactory());
    webUrls.stream()
        .peek(
            url -> {
              if (log.isInfoEnabled()) {
                log.info("Crawling the URL: {}", url);
              }
              Document doc =
                  Try.of(() -> Jsoup.connect(url).get())
                      .getOrElseThrow(
                          e ->
                              new ResponseStatusException(
                                  HttpStatus.BAD_REQUEST, String.format("Invalid Url %s", url)));
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
              SolrDocument solrDocument = new SolrDocument();
              links.stream()
                  .distinct()
                  .forEach(link -> solrDocument.setField(link.attr("href"), link.text()));

              webCrawlRepository.save(
                  WebCrawlDetails.builder()
                      // .id(UUID.randomUUID().toString())
                      .title(doc.title())
                      .keyword(Arrays.asList(StringUtils.split(keywords, ",")))
                      .description(description)
                      .linkedUrls(solrDocument.toString())
                      .build());
            })
        .collect(Collectors.toList());
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
