package com.project.notes_v2.security;

import com.project.notes_v2.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {
    private final AccountRepository accountRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;


    /**
     * Returns a BCryptPasswordEncoder bean to securely hash and verify passwords
     *  using the BCrypt hashing algorithm.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * Returns a custom UserDetailsService bean that provides the mechanism
     *  for loading user-specific data required during the authentication process.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailsService;
    }


    /**
     * Returns a DaoAuthenticationProvider that uses a custom UserDetailsService and PasswordEncoder
     *  to handle user authentication.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }


    /**
     * handling successful authentication events.
     * defines custom actions to be executed after a user has successfully logged in.
     */
    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler(this.accountRepository);
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    /**
     * Configures and returns a SecurityFilterChain bean
     *   to define the security settings for HTTP requests in the application.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource( corsConfigurationSource() ) )
            .csrf(csrf -> csrf.disable())
            .authorizeRequests(request -> {
                request.requestMatchers(new AntPathRequestMatcher("/api/accounts","POST")).permitAll();
                request.requestMatchers(new AntPathRequestMatcher("/api/authentication/login")).permitAll();
                request.requestMatchers(new AntPathRequestMatcher("/api/authentication/successLogout")).permitAll();
                request.anyRequest().authenticated();

            })
            .formLogin(form -> {
                form.loginPage("/login");
                form.permitAll();
                form.loginProcessingUrl("/api/authentication/login");
                form.successHandler(customAuthenticationSuccessHandler());
                form.failureUrl("/login?error=true");
                form.usernameParameter("username");
                form.passwordParameter("password");
            })
            .logout(form -> {
                form.logoutUrl("/api/authentication/logout");
                form.logoutSuccessUrl("/api/authentication/successLogout");
                form.invalidateHttpSession(true);
                form.deleteCookies("JSESSIONID");
            })
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
        return http.build();
    }


    /**
     * cors configuration
     */
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList("https://www.getpostman.com", "http://localhost:4200/"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(3600L);
        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
        corsConfiguration.setExposedHeaders(Arrays.asList("Content-Disposition"));
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return corsConfigurationSource;
    }

}
