package com.example.poc.client.controller;

import com.example.poc.client.domain.Request;
import com.example.poc.client.domain.Response;
import com.example.poc.client.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ClientController {

  private  final WebClient webClient;
  private  final CloseableHttpClient httpClient;
  private  final ObjectMapper objectMapper;

  private List<Request> requestList = IntStream.range(0, 5000)
      .mapToObj(value -> new Request(String.valueOf(value)))
      .collect(Collectors.toList());


  @GetMapping(value = "/get-with-web-client")
  public Mono<Response> getWithWebClient(@RequestBody Request request) {
    return webClient.post()
        .uri("test")
        .body(BodyInserters.fromObject(request))
        .retrieve()
        .bodyToMono(Response.class);
  }

  @GetMapping(value = "/get-with-rest-template")
  public Mono<Response> getWithRestTemplate(@RequestBody Request request) {
    return webClient.post()
        .uri("test")
        .body(BodyInserters.fromObject(request))
        .retrieve()
        .bodyToMono(Response.class);
  }


  @GetMapping(value = "/fire-with-web-client")
  public Mono<Void> fireWithWebClient() {
    return Flux.fromStream(requestList.stream())
           // .doOnNext(System.out::println)
            .map(request -> webClient.post()
            .uri("test")
            .body(BodyInserters.fromObject(request))
            .retrieve()
            .bodyToMono(Response.class))
        .then();
  }

  @GetMapping(value = "/fire-with-rest-template")
  public void fireWithRestTemplate() {
    requestList
        .parallelStream()
        .forEach(request -> new RestTemplate()
            .postForObject("http://127.0.0.1:8080/test", request, Response.class));
  }


  @GetMapping(value = "/fire-with-apache-http-client")
  public void fireWithApacheHttpClient() throws IOException {
    requestList
        .parallelStream()
        .map(this::writeAsString)
        .map(this::prepareStringEntity)
        .forEach(this::prepareTrigger);
  }

  @GetMapping(value = "/fetch-users")
  public Mono<Void> fetchUsers() {

    return Flux.fromStream(IntStream.range(5, 20).boxed())
        .flatMap(integer -> webClient.post()
            .uri(uriBuilder -> uriBuilder.path("fetch")
                .queryParam("id", integer)
                .build())
            .retrieve()
            .bodyToMono(User.class))
        .doOnNext(System.out::println)
        .then();
  }

  private String writeAsString(Request request){
    try {
      return objectMapper.writeValueAsString(request);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }
  }

  private StringEntity prepareStringEntity(String jsonString){
    try {
      return new StringEntity(jsonString);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return null;
    }
  }

  private void prepareTrigger(StringEntity entity){
    HttpPost httpPost = new HttpPost("http://127.0.0.1:8080/test");
    httpPost.setEntity(entity);
    httpPost.setHeader("Content-type", "application/json");

    try {
      CloseableHttpResponse response = httpClient.execute(httpPost);
      response.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
