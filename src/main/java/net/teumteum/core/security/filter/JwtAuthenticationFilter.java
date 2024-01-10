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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    private static void saveUserAuthentication(User user) {
        UserAuthentication authentication = new UserAuthentication(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getMethod().equals("OPTIONS")) {
            return;
        }

        try {
            String token = this.resolveTokenFromRequest(request);
            if (checkTokenExistenceAndValidation(token)) {
                User user = getUser(token);
                saveUserAuthentication(user);
            }
        } catch (InsufficientAuthenticationException e) {
            log.error("JwtAuthentication UnauthorizedUserException!");
        }
        filterChain.doFilter(request, response);
    }

    private User getUser(String token) {
        return this.authService.findUserByToken(token)
                .orElseThrow(() -> new UsernameNotFoundException("일치하는 회원 정보가 존재하지 않습니다."));
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
