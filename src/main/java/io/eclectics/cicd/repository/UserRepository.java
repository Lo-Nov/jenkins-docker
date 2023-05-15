package io.eclectics.cicd.repository;

import io.eclectics.cicd.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(countQuery = "select count(u) from User u where u.softDelete = false")
    Page<User> findAllBySoftDeleteFalse(Pageable pageable);

    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findById(@NotNull Long id);
}
