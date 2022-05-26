package com.dbs.web.crawler.vo;

import lombok.*;

import java.util.List;

/** * Response structure to represent error/exception scenarios. */
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
@Getter
public class ErrorResponse {
    private List<String> messages;
    private String status;
}
