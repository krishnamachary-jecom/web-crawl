package com.dbs.web.crawler.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SolrDocument(collection = "webcrawl_details")
public class WebCrawlDetails {
  @Id @Indexed @EqualsAndHashCode.Include private String id;

  @Indexed private String title;

  @Indexed private List<String> keyword;

  @Indexed private String description;

  @Indexed private String linkedUrls;
}
