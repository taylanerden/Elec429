package com.example.poc.service.controller;

import com.example.poc.service.domain.Request;
import com.example.poc.service.domain.Response;
import com.example.poc.service.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@RestController
@RequiredArgsConstructor
public class ServiceController {

    private Scheduler scheduler = Schedulers.newParallel("service", 4);
    private final ReactiveRedisOperations<String, User> reactiveRedisOperations;

    @PostMapping(value = "/test")
    public Mono<Response> test(@RequestBody Request request) {
        return Mono.just(new Response("Hello " + request.getRequestBody()))
                .doOnNext(response -> System.out.println(response))
                .subscribeOn(scheduler);
    }

    @PostMapping(value = "/test2")
    public Response test2(@RequestBody Request request) {
        return new Response("Hello " + request.getRequestBody());
    }

    @PostMapping(value = "/bulk-put")
    public Mono<Boolean> bulkPut(@RequestBody User user) {
        Map<String, User> map = IntStream.range(Integer.parseInt(user.getId()), Integer.parseInt(user.getId()) + 250000)
                .mapToObj(id -> new User(String.valueOf(id), user.getFullName(), user.getUserName(), user.getGsmNo(), user.getEmail(), user.getPassword()))
                .collect(HashMap::new, (m, u) -> m.put(u.getId(), u), Map::putAll);

        return reactiveRedisOperations.opsForHash().putAll("USER_CACHE", map);
    }

    @PostMapping(value = "/fetch")
    public Mono<User> fetch(@RequestParam(value = "id") String id) {
        return Mono.defer(() -> {
            if (id.equals("11") || id.equals("12") || id.equals("13")) {
                return reactiveRedisOperations.opsForHash().values("USER_CACHE")
                        .map(User.class::cast)
                        .then(Mono.just(new User(id, "Taylan erden",
                                "terden", "5319343550", "terden14@ku.edu.tr",
                                "123321")));
            } else {
                return reactiveRedisOperations.opsForHash().get("USER_CACHE", id)
                        .map(User.class::cast);
            }
        });
    }
}
