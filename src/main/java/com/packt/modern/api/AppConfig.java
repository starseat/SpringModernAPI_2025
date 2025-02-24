package com.packt.modern.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

/**
 * @author : github.com/sharmasourabh
 * @project : Chapter04 - Modern API Development with Spring and Spring Boot Ed 2
 **/
@Configuration
public class AppConfig {
  @Bean
  public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
    // append ETag
    return new ShallowEtagHeaderFilter();
  }
}
