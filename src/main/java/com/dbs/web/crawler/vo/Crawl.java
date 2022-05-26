package com.dbs.web.crawler.vo;

import lombok.*;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
@Getter
public class Crawl {

    List<String> webUrls;
}
