package com.dbs.web.crawler.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
public class PaginationConfig {

  @Autowired PaginationProperties properties;

  /**
   * Set pagination to begin from page 1.
   *
   * @return
   */
  @Bean
  public PageableHandlerMethodArgumentResolverCustomizer pageableResolverCustomizer() {
    return new PageableHandlerMethodArgumentResolverCustomizer() {
      @Override
      public void customize(
          PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver) {
        pageableHandlerMethodArgumentResolver.setOneIndexedParameters(true);
        pageableHandlerMethodArgumentResolver.setMaxPageSize(properties.getMaxPageSize());
        pageableHandlerMethodArgumentResolver.setFallbackPageable(
            PageRequest.of(0, properties.getDefaultPageSize()));
      }
    };
  }
}
