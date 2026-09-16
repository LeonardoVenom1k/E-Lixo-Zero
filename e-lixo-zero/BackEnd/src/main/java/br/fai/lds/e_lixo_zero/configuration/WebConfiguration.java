package br.fai.lds.e_lixo_zero.configuration;

import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.user.UserService;
import br.fai.lds.e_lixo_zero.security.JwtAuthenticationFilter;
import br.fai.lds.e_lixo_zero.security.JwtTokenService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfiguration {

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(final JwtTokenService jwtTokenService, final UserService userService) {
        final FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtAuthenticationFilter(jwtTokenService, userService));
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1);
        return registration;
    }
}
