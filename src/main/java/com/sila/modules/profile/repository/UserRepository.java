package com.sila.modules.profile.repository;

import com.sila.modules.profile.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    public User findByEmail(String username);

    List<User> findAllByCreatedAtIsNullOrUpdatedAtIsNull();

}
