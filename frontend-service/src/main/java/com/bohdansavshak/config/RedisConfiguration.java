package com.bohdansavshak.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
public class RedisConfiguration {

  @Bean
  public ReactiveRedisOperations<String, Object> redisOperations(
      ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {

    RedisSerializationContext<String, Object> serializationContext =
        RedisSerializationContext.<String, Object>newSerializationContext(
                new StringRedisSerializer())
            .key(new StringRedisSerializer())
            .hashKey(new StringRedisSerializer())
            .hashValue(new GenericJackson2JsonRedisSerializer())
            .build();

    return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, serializationContext);
  }
}
