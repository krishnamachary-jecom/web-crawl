package com.dbs.web.crawler.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
@Getter(value = AccessLevel.PUBLIC)
@JsonInclude(Include.NON_NULL)
public class ApiResponse<T> {
  private boolean success;
  private String message;
  private T data;
}
