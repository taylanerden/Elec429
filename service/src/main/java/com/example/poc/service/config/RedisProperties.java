package com.example.poc.service.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Taylan Erden
 */
@Getter
@Setter
@ToString
@ConfigurationProperties(value = "spring.data.redis")
public class RedisProperties {

  private String hostname;

  private int port;

  private String password;
}