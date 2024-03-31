package com.angelinadarenina.crudRestControllers.service;


import com.angelinadarenina.crudRestControllers.model.Role;
import com.angelinadarenina.crudRestControllers.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<User> listUsers();

    User getUserByID(long id);

    Set<Role> getRolesById(long id);

    void save(User user);

    void update(long id, User updatedUser);

    void delete(long id);
}
