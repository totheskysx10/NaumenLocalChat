package ru.naumen.naumenlocalchat.extern.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.naumen.naumenlocalchat.exception.AuthException;
import ru.naumen.naumenlocalchat.extern.infrastructure.service.SecurityContextService;

/**
 * Конфигурация Spring Security
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SecurityContextService securityContextService;

    public SecurityConfig(SecurityContextService securityContextService) {
        this.securityContextService = securityContextService;
    }

    /**
     * Кодировщик паролей
     */
    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Конфигурация цепочки фильтров защиты
     *
     * @param http http
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.
                authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/login", "/users/register", "/register.html").anonymous()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/users/admin", "/users/no-admin").hasRole("ADMIN")
                        .requestMatchers("/reports/generate", "/reports/user-reports")
                        .access((authentication, context) -> {
                            Long userId = Long.parseLong(context.getRequest().getParameter("userId"));
                            try {
                                return new AuthorizationDecision(securityContextService.isCurrentAuthId(userId));
                            } catch (AuthException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .requestMatchers("/users/reset-password", "/users/request-reset-password",
                                "/users/confirm-email", "/users/request-confirm-email", "/styles.css").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
