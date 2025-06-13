package com.example.springDemo.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/students/postgres/**").hasRole("ADMIN")
            .requestMatchers("/api/students/mysql/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        // 替换过时的httpBasic()方法
        .httpBasic(withDefaults()); // 使用withDefaults()静态方法

    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails adminUser = User.builder()
        .username("admin")
        .password(passwordEncoder().encode("admin"))
        .roles("ADMIN")
        .build();

    System.out.println("创建内存中的用户: " + adminUser.getUsername());
    System.out.println("角色: " + adminUser.getAuthorities());

    return new InMemoryUserDetailsManager(adminUser);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}