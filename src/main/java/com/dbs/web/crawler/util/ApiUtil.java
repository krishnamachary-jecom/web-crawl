package com.dbs.web.crawler.util;

import com.dbs.web.crawler.vo.ApiResponse;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static io.vavr.API.*;

public class ApiUtil {

    public static <T> ApiResponse<T> response(boolean isSuccess, String message, T data) {
        return ApiResponse.<T>builder().success(isSuccess).message(message).data(data).build();
    }

    public static <T> ApiResponse<List<T>> createResponse(String message, List<T> data) {
        return Match(data)
                .of(
                        Case($(CollectionUtils::isNotEmpty), createSuccessResponse(message, data)),
                        Case($(), createFailureResponse("No data found")));
    }

    public static <T> ApiResponse<T> createResponse(String message, T data) {
        return Match(data)
                .of(
                        Case($(Objects::nonNull), createSuccessResponse(message, data)),
                        Case($(), createFailureResponse("No data found")));
    }

    public static <T> ApiResponse<T> createSuccessResponse(String message, T data) {
        return response(true, message, data);
    }

    public static <T> ApiResponse<Optional<T>> createEmptySuccessResponse(String message) {
        return createSuccessResponse(message, Optional.empty());
    }

    public static <T> ApiResponse<T> createFailureResponse(String message) {
        return response(false, message, null);
    }
}
