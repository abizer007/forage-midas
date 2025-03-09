package com.jpmc.midascore;

import com.jpmc.midascore.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository1 extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId);
}