package net.teumteum.core.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.teumteum.core.property.JwtProperty;
import net.teumteum.core.security.UserAuthentication;
import net.teumteum.core.security.service.AuthService;
import net.teumteum.core.security.service.JwtService;
import net.teumteum.user.domain.User;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final AuthService authService;
    private final JwtProperty jwtProperty;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        /* Cors Preflight Request */
        if (request.getMethod().equals("OPTIONS")) {
            return;
        }

        try {
            String token = this.resolveTokenFromRequest(request);
            if (checkTokenExistenceAndValidation(token)) {
                User user = this.authService.findUserByToken(token).get();
                UserAuthentication authentication = new UserAuthentication(user);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (InsufficientAuthenticationException e) {
            log.info("JwtAuthentication UnauthorizedUserException!");
        }
        filterChain.doFilter(request, response);
    }

    private boolean checkTokenExistenceAndValidation(String token) {
        return StringUtils.hasText(token) && this.jwtService.validateToken(token);
    }

    private String resolveTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(jwtProperty.getAccess().getHeader());
        if (!ObjectUtils.isEmpty(token) && token.toLowerCase().startsWith(jwtProperty.getBearer().toLowerCase())) {
            return token.substring(jwtProperty.getBearer().length()).trim();
        }
        return null;
    }
}
