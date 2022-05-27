package com.dbs.web.crawler;

import com.dbs.web.crawler.config.PaginationConfig;
import com.dbs.web.crawler.config.PaginationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

@SpringBootApplication
@EnableSolrRepositories
@EnableConfigurationProperties({PaginationProperties.class})
@EnableAspectJAutoProxy()
@Import({PaginationConfig.class})
public class WebCrawlerApplication {

  public static void main(String[] args) {
    SpringApplication.run(WebCrawlerApplication.class, args);
  }
}
