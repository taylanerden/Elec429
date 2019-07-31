package com.example.poc.client.config;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BeanConfig {

  @Bean
  public WebClient webClient() {
    return WebClient.create("http://127.0.0.1:8080");
  }

  @Bean
  public CloseableHttpClient httpClient() {
    return HttpClientBuilder.create().build();
  }
}
