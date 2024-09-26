package me.siansxint.hhrr.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final AuthenticationProvider provider;

    @Autowired
    public SecurityConfiguration(AuthenticationProvider provider) {
        this.provider = provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry ->
                        registry
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                                .permitAll()
                                .requestMatchers(
                                        "/auth/login",
                                        "/auth/register",
                                        "/h2-console/**"
                                )
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .authenticationProvider(provider)
                .headers(configurer ->
                        configurer
                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .formLogin(configurer ->
                        configurer
                                .defaultSuccessUrl("/", true)
                                .loginPage("/auth/login")
                                .permitAll())
                .logout(configurer ->
                        configurer.logoutSuccessUrl("/auth/login")
                                .permitAll())
                .build();
    }
}