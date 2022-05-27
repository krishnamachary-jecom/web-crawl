package com.dbs.web.crawler.vo;

import org.apache.commons.collections4.CollectionUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.List;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SSLResolver {
    String urlPath();
}
