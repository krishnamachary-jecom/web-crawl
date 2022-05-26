package com.dbs.web.crawler.vo;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
@Getter
public class Crawl {

  @NotEmpty(message = "URL must not be empty")
  List<String> webUrls;
}
