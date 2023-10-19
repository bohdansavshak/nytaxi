package com.bohdansavshak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableRedisRepositories
public class TotalCalculator {
  public static void main(String[] args) {
    SpringApplication.run(TotalCalculator.class, args);
  }
}
