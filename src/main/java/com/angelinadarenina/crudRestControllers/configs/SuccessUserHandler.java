package com.angelinadarenina.crudRestControllers.configs;

import com.angelinadarenina.crudRestControllers.model.User;
import com.angelinadarenina.crudRestControllers.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Component
public class SuccessUserHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;

    @Autowired
    public SuccessUserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> currentUserOptional = userRepository.findByUsername(userDetails.getUsername());
        User currentUser = currentUserOptional.orElseThrow(NoSuchElementException::new);
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (roles.contains("ROLE_ADMIN")) {
            httpServletResponse.sendRedirect("/admin/getAllUsers");
        } else if (roles.contains("ROLE_USER")) {
            Long userId = currentUser.getId();
            httpServletResponse.sendRedirect("/user/getProfile/" + userId);
        } else {
            httpServletResponse.sendRedirect("/");
        }
    }
}