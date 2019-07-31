package com.example.poc.service.config;

import com.example.poc.service.domain.User;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author Taylan Erden
 */
@Configuration
@EnableConfigurationProperties(value = RedisProperties.class)
public class BeanConfig {

  @Bean
  public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(RedisProperties redisProperties) {
    RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
    configuration.setHostName(redisProperties.getHostname());
    configuration.setPort(redisProperties.getPort());
    configuration.setPassword(RedisPassword.of(redisProperties.getPassword()));
    return new LettuceConnectionFactory(configuration);
  }

  @Bean
  public ReactiveRedisOperations<String, User> reactiveRedisOperations(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
    RedisSerializationContext.RedisSerializationContextBuilder<String, User> builder = RedisSerializationContext.newSerializationContext();

    RedisSerializationContext<String, User> context = builder
        .key(new StringRedisSerializer())
        .value(new Jackson2JsonRedisSerializer<>(User.class))
        .hashKey(new StringRedisSerializer())
        .hashValue(new Jackson2JsonRedisSerializer<>(User.class))
        .build();

    return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, context);
  }
}
