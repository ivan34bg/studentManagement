package org.studentmanagement.utilities;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public class RequestHelper {
    public static String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.split(" ")[1];
        }

        return null;
    }
}
