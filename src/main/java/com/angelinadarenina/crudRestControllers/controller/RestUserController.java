package com.angelinadarenina.crudRestControllers.controller;


import com.angelinadarenina.crudRestControllers.dto.RoleDTO;
import com.angelinadarenina.crudRestControllers.dto.UserDTO;
import com.angelinadarenina.crudRestControllers.model.Role;
import com.angelinadarenina.crudRestControllers.model.User;
import com.angelinadarenina.crudRestControllers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RestUserController {
    private final UserService userService;


    @Autowired
    public RestUserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/admin/getAllUsers")
    public List<UserDTO> showAllUsers() {
        List<User> users = userService.listUsers();
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            userDTOs.add(convertToUserDto(user));
        }
        return userDTOs;
    }

    @GetMapping("/user/getProfile/{id}")
    public ResponseEntity<UserDTO> showUserProfile(@PathVariable("id") int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = (User) userDetails;
        if (currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            User userFromService = userService.getUserByID(id);
            return ResponseEntity.ok(convertToUserDto(userFromService));
        } else if (currentUser.getId().equals((long) id)) {
            User userFromService = userService.getUserByID(id);
            return ResponseEntity.ok(convertToUserDto(userFromService));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/admin/createUser")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        userService.save(user);
        return ResponseEntity.ok(convertToUserDto(user));
    }

    @PatchMapping("/admin/updateUser/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") Long id, @RequestBody User updatedUser) {
        User existingUser = userService.getUserByID(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        if (updatedUser.getUsername() != null) {
            existingUser.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getLastName() != null) {
            existingUser.setLastName(updatedUser.getLastName());
        }
        if (updatedUser.getAge() != 0) {
            existingUser.setAge(updatedUser.getAge());
        }
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPassword() != null) {
            existingUser.setPassword(updatedUser.getPassword());
        }
        if (updatedUser.getRoles() != null) {
            existingUser.setRoles(updatedUser.getRoles());
        }
        userService.update(id, existingUser);
        return ResponseEntity.ok(convertToUserDto(existingUser));
    }

    @DeleteMapping("/admin/deleteUser/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") int id) {
        userService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    // --------------конвертация в DTO--------------------

    private UserDTO convertToUserDto(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setLastName(user.getLastName());
        userDTO.setAge(user.getAge());
        userDTO.setEmail(user.getEmail());
        List<RoleDTO> roleDTOs = user.getRoles().stream()
                .map(this::convertToRoleDTO)
                .collect(Collectors.toList());
        userDTO.setRoles(roleDTOs);
        return userDTO;
    }

    private RoleDTO convertToRoleDTO(Role role) {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(role.getId());
        roleDTO.setName(role.getName());
        return roleDTO;
    }
}
