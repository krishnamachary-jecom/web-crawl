package com.dbs.web.crawler.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dbs.pagination")
@Setter
@Getter
public class PaginationProperties {
    private int maxPageSize;
    private int defaultPageSize;
}
