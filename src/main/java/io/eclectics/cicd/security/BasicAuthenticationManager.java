package io.eclectics.cicd.security;

import io.eclectics.cicd.security.user.UserEntity;
import io.eclectics.cicd.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

/**
 * Responsible for handling user login.
 * Uses the Reactive Authentication Manager.
 */
@Slf4j
@AllArgsConstructor
public class BasicAuthenticationManager implements ReactiveAuthenticationManager {

    private final ReactiveUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    public BasicAuthenticationManager(ReactiveUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        log.info("The user -> " + authentication.getPrincipal());
        if (authentication.isAuthenticated()) return Mono.just(authentication);

        return checkUser((String) authentication.getPrincipal(), (String) authentication.getCredentials())
                .map(userEntity -> toUsernamePasswordAuthenticationToken(authentication, userEntity));
    }

    public Mono<UserEntity> checkUser(String username, String credentials) {
        return userService.findByUsername(username)
                .flatMap(userEntity -> validateUser(userEntity, credentials))
                .switchIfEmpty(Mono.defer(this::raiseBadCredentials));
    }

    public Mono<UserEntity> validateUser(UserEntity user, String credentials) {
        log.info("Validating user {} with credentials {}", user.getUsername(), credentials);
        if (user.getLocked() == 1) return Mono.defer(this::raiseAccountLocked);
        if (!passwordEncoder.matches(credentials, user.getPassword())) {

            int trials = user.getTrials() + 1;

            user.setTrials(trials);
            user.setLocked((short) (trials > 3 ? 1 : 0));
            userService.saveUser(user);

            return Mono.defer(() -> raiseTrialsIncreased(3 - trials));
        }
        return Mono.just(user);
    }

    private UsernamePasswordAuthenticationToken toUsernamePasswordAuthenticationToken(Authentication authentication, UserEntity user) {
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return new UsernamePasswordAuthenticationToken(
                authentication.getPrincipal(),
                authentication.getCredentials(),
                authorities
        );
    }

    private <T> Mono<T> raiseBadCredentials() {
        return Mono.error(new BadCredentialsException("Bad credentials!!"));
    }

    private <T> Mono<T> raiseAccountLocked() {
        return Mono.error(new BadCredentialsException("Account is locked"));
    }

    private <T> Mono<T> raiseTrialsIncreased(int trials) {
        return Mono.error(new BadCredentialsException("Wrong password. You have " + trials + " attempts left."));
    }

}
