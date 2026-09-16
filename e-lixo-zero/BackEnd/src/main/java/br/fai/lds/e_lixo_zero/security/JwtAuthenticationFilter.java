package br.fai.lds.e_lixo_zero.security;

import br.fai.lds.e_lixo_zero.domain.UserModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UserService userService;

    public JwtAuthenticationFilter(final JwtTokenService jwtTokenService, final UserService userService) {
        this.jwtTokenService = jwtTokenService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        final String requestPath = request.getRequestURI();
        final String method = request.getMethod();

        setCorsHeaders(response);

        if ("OPTIONS".equalsIgnoreCase(method)) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        if (isPublicPath(requestPath, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        final String token = authHeader.substring(7);
        if (!jwtTokenService.isValidToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        final String email = jwtTokenService.getSubjectFromToken(token);
        final UserModel user = userService.findByEmail(email);
        if (user == null || !user.isActive()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        request.setAttribute("email", email);
        filterChain.doFilter(request, response);
    }

    private void setCorsHeaders(final HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private boolean isPublicPath(final String path, final String method) {
        if (path.startsWith("/api/users/login")) {
            return true;
        }
        if ("/api/users".equals(path) && "POST".equalsIgnoreCase(method)) {
            return true;
        }
        if (path.startsWith("/api/waste-types") && "GET".equalsIgnoreCase(method)) {
            return true;
        }
        if (path.startsWith("/api/collection-points") && "GET".equalsIgnoreCase(method)) {
            return true;
        }
        return path.startsWith("/h2-console");
    }
}
