package com.example.poc.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClientApplication {

  public static void main(String[] args) {
    SpringApplication.run(ClientApplication.class, args);
    System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "1");
  }
}
