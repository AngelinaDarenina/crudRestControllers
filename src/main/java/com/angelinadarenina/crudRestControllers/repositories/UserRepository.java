package com.angelinadarenina.crudRestControllers.repositories;


import com.angelinadarenina.crudRestControllers.model.Role;
import com.angelinadarenina.crudRestControllers.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    @Query("SELECT u.roles FROM User u WHERE u.id = :userId")
    Set<Role> getRolesById(@Param("userId") long userId);
}
