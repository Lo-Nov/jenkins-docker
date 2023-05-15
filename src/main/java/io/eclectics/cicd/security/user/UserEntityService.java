package io.eclectics.cicd.security.user;

import reactor.core.publisher.Mono;

public interface UserEntityService {
    Mono<UserEntity> findByUsername(String username);

    UserEntity saveUser(UserEntity user);
}
