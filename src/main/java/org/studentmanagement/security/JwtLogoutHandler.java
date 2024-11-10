package org.studentmanagement.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.studentmanagement.services.UserService;
import org.studentmanagement.utilities.RequestHelper;

@Component
public class JwtLogoutHandler implements LogoutHandler {
    private final UserService userService;

    public JwtLogoutHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        userService.logout(RequestHelper.extractTokenFromRequest(request));
        SecurityContextHolder.clearContext();
    }
}
