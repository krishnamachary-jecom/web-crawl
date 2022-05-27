package com.dbs.web.crawler.config;

import com.dbs.web.crawler.vo.SSLResolver;
import lombok.extern.slf4j.Slf4j;
import nl.altindag.ssl.SSLFactory;
import nl.altindag.ssl.util.CertificateUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.lang.reflect.Method;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.vavr.API.*;

/**
 * Aspect to fetch SSL for the secured Urls and load the certificate to Trusted Manager to avoid SSL
 * Handshake Exception.
 */
@Component
@Aspect
@Slf4j
public class SSLResolverAspect {
  @Before("@annotation(com.dbs.web.crawler.vo.SSLResolver)")
  public void before(JoinPoint joinPoint) {
    log.info("Check for SSL");
    List<String> webUrls = getWebUrls(joinPoint);
    List<String> securedUrls =
        webUrls.stream()
            .filter(webUrl -> StringUtils.startsWith(webUrl, "https://"))
            .collect(Collectors.toList());
    Match(securedUrls)
        .of(
            Case(
                $(count -> securedUrls.size() > 0),
                function ->
                    run(
                        () -> {
                          // TODO Should be loaded to safe trust manager
                          log.info("Loading certificates to trust manager");
                          Map<String, List<X509Certificate>> certificates =
                              CertificateUtils.getCertificate(securedUrls);
                          List<Certificate> certs =
                              certificates.values().stream()
                                  .flatMap(List::stream)
                                  .collect(Collectors.toList());
                          SSLFactory sslFactory =
                              SSLFactory.builder().withTrustMaterial(certs).build();
                          HttpsURLConnection.setDefaultSSLSocketFactory(
                              sslFactory.getSslSocketFactory());
                        })),
            Case(
                $(),
                function ->
                    run(() -> log.info("No secured urls found. Proceeding for web crawl."))));
  }

  private List<String> getWebUrls(JoinPoint joinPoint) {
    // Get Method arguments
    Object[] args = joinPoint.getArgs();
    // Get Annotation
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    SSLResolver sslResolver = method.getAnnotation(SSLResolver.class);
    // Expression Parser to fetch url value
    ExpressionParser elParser = new SpelExpressionParser();
    Expression expression = elParser.parseExpression(sslResolver.urlPath());
    // Web urls to generate ssl
    return (List<String>) expression.getValue(args);
  }
}
