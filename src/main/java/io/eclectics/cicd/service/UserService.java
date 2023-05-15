package io.eclectics.cicd.service;

import io.eclectics.cicd.model.User;
import io.eclectics.cicd.others.PaginationWrapper;
import io.eclectics.cicd.others.UniversalResponse;
import io.eclectics.cicd.others.UserWrapper;
import io.eclectics.cicd.security.user.UserEntity;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface UserService {
    Mono<UserEntity> findByUsername(String username);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findById(Long id);

    User createUpdateUser(User user);

    UserEntity saveUser(UserEntity user);

    Mono<UniversalResponse> createUser(UserWrapper wrapper, String createdBy);

    Mono<UniversalResponse> updateUser(Long id, UserWrapper wrapper, String modifiedBy);

    Mono<UniversalResponse> deleteUser(Long id, String username);

    Mono<UniversalResponse> getById(Long id, String username);

    Mono<UniversalResponse> getAll(PaginationWrapper wrapper, String username);
}
