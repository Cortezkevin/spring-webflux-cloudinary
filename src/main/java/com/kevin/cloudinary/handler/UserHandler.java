package com.kevin.cloudinary.handler;

import com.kevin.cloudinary.documents.User;
import com.kevin.cloudinary.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import org.springframework.http.codec.multipart.Part;

@Component
@RequiredArgsConstructor
public class UserHandler {

    private final UserService service;

    public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body( service.getAll(), User.class);
    }

    public Mono<ServerResponse> getById(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body( service.getById(id), User.class);
    }

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        Mono< MultiValueMap<String, Part>> multiValueMapMono = serverRequest.multipartData();
        return multiValueMapMono.flatMap( multipartData -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body( service.create( multipartData ), User.class));
    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<User> userMono = serverRequest.bodyToMono(User.class);
        return userMono.flatMap( user -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body( service.update(user, id), User.class));
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body( service.delete(id), User.class);
    }

}
