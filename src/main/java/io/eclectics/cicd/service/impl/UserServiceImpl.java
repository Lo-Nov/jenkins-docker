package io.eclectics.cicd.service.impl;

import io.eclectics.cicd.model.User;
import io.eclectics.cicd.others.PaginationWrapper;
import io.eclectics.cicd.others.UniversalResponse;
import io.eclectics.cicd.others.UserWrapper;
import io.eclectics.cicd.repository.UserRepository;
import io.eclectics.cicd.security.user.UserEntity;
import io.eclectics.cicd.security.user.UserEntityRepository;
import io.eclectics.cicd.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

//A Lombok annotation in Java that automatically generates a constructor for a class with final fields as arguments
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserEntityRepository userEntityRepository;

    // private final keywords are used together to declare a constant or an immutable field in a class.
    @Lazy
    @Autowired
    private UserService userService;
    private final UserRepository userRepository;

    @Override
    public Mono<UserEntity> findByUsername(String username) {
        return Mono.fromCallable(() -> findUserByUsername(username).orElse(null))
                .publishOn(Schedulers.boundedElastic());
    }

    private Optional<UserEntity> findUserByUsername(String username) {
        return userEntityRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User createUpdateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public UserEntity saveUser(UserEntity user) {
        return userEntityRepository.save(user);
    }

    @Override
    public Mono<UniversalResponse> createUser(UserWrapper wrapper, String createdBy) {
        return Mono.fromCallable(() -> {
            // Validate logged user
            UniversalResponse User_not_found = validateLoggedUser(createdBy);
            if (User_not_found != null) return User_not_found;

            //check for duplicates
            Optional<User> optionalUser = userService.findByPhoneNumber(wrapper.getPhoneNumber());
            if (optionalUser.isPresent()) return new UniversalResponse("01", "Duplicate found");

            // instantiate a user object or builder from the user entity
            User userBody = User.builder()
                    .age(wrapper.getAge())
                    .active(true)
                    .firstName(wrapper.getFirstName())
                    .lastName(wrapper.getLastName())
                    .nationalId(wrapper.getNationalId())
                    .phoneNumber(wrapper.getPhoneNumber())
                    .build();
            //save user
            createUpdateUser(userBody);

            return new UniversalResponse("00", "User created successfully !");
        }).publishOn(Schedulers.boundedElastic());
    }

    @Nullable
    private UniversalResponse validateLoggedUser(String createdBy) {
        //validate the modifier first
        Optional<User> createBy = userService.findByPhoneNumber(createdBy);
        if (createBy.isEmpty()) return new UniversalResponse("01", "User not found");
        return null;
    }

    @Override
    public Mono<UniversalResponse> updateUser(Long id, UserWrapper wrapper, String createdBy) {
        return Mono.fromCallable(() -> {
            //validate the modifier first
            UniversalResponse User_not_found = validateLoggedUser(createdBy);
            if (User_not_found != null) return User_not_found;

            //check if the user to be updated is present
            Optional<User> userOptional = userService.findById(id);
            if (userOptional.isEmpty()) return new UniversalResponse("01", "User not found");

            User user = userOptional.get();

            //update our user
            user.setFirstName(wrapper.getFirstName());
            user.setAge(wrapper.getAge());
            user.setLastName(wrapper.getLastName());
            user.setPhoneNumber(wrapper.getPhoneNumber());

            //update user
            userService.createUpdateUser(user);
            return new UniversalResponse("00", "User updated successfully !");

        }).publishOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<UniversalResponse> deleteUser(Long id, String username) {
        return Mono.fromCallable(()->{
            UniversalResponse User_not_found = validateLoggedUser(username);
            if (User_not_found != null) return User_not_found;

            //check if the user to be updated is present
            Optional<User> userOptional = userService.findById(id);
            if (userOptional.isEmpty()) return new UniversalResponse("01", "User not found");
            User user = userOptional.get();

            userRepository.delete(user);
            return new UniversalResponse("00", "User deleted successfully !");

        }).publishOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<UniversalResponse> getById(Long id, String username) {
        return Mono.fromCallable(()->{
            UniversalResponse User_not_found = validateLoggedUser(username);
            if (User_not_found != null) return User_not_found;

            //check if the user to be updated is present
            Optional<User> userOptional = userService.findById(id);
            if (userOptional.isEmpty()) return new UniversalResponse("01", "User not found");
            User user = userOptional.get();

            return new UniversalResponse("00", "User", user);

        }).publishOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<UniversalResponse> getAll(PaginationWrapper wrapper, String username) {
        return Mono.fromCallable(()->{
            UniversalResponse User_not_found = validateLoggedUser(username);
            if (User_not_found != null) return User_not_found;

            PageRequest pageRequest = PageRequest.of(wrapper.getPage(), wrapper.getSize());
            Page<User> userPage = userRepository.findAllBySoftDeleteFalse(pageRequest);

            List<UserWrapper> collect = userPage.getContent()
                    .stream()
                    .map(this::mapToUserWrapper)
                    .collect(Collectors.toList());
            return new UniversalResponse("00", "Users", collect);

        }).publishOn(Schedulers.boundedElastic());
    }
    private UserWrapper mapToUserWrapper(User user) {
        return UserWrapper.builder()
                .id(user.getId())
                .nationalId(user.getNationalId())
                .phoneNumber(user.getPhoneNumber())
                .active(user.isActive())
                .age(user.getAge())
                .build();
    }

}
