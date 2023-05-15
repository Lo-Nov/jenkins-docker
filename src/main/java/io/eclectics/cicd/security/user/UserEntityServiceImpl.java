package io.eclectics.cicd.security.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserEntityServiceImpl implements UserEntityService {

    private final UserEntityRepository userEntityRepository;
    @Override
    public Mono<UserEntity> findByUsername(String username) {
        return Mono.fromCallable(() -> findUserByUsername(username).orElse(null))
                .publishOn(Schedulers.boundedElastic());
    }

    private Optional<UserEntity> findUserByUsername(String username) {
        return userEntityRepository.findByUsername(username);
    }

    @Override
    public UserEntity saveUser(UserEntity user) {
        return userEntityRepository.save(user);
    }
}
