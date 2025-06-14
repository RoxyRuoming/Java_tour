package com.example.springDemo.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.example.springDemo.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  // 删除字段注入 - 因为这样会导致循环依赖问题 -> 使用方法注入替代字段注入
  // @Autowired
  // private UserServiceImpl userService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, UserServiceImpl userService) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/login", "/oauth2/**", "/error", "/webjars/**").permitAll()
            .requestMatchers("/api/students/postgres/**").authenticated() // 只要认证即可，不需要ADMIN角色
            .requestMatchers("/api/students/mysql/**").authenticated()
            .anyRequest().authenticated()
        )
        // 配置 OAuth2 登录
        .oauth2Login(oauth2 -> oauth2
            .loginPage("/login")
            .userInfoEndpoint(userInfo -> userInfo
                .userService(userService)
            )
            .successHandler(oauth2AuthenticationSuccessHandler())
        )
        // 添加表单登录支持
        .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/")
            .permitAll()
        )
        // 保留 Basic Auth 用于 API 测试
        .httpBasic(withDefaults())
        // 配置登出
        .logout(logout -> logout
            .logoutSuccessUrl("/")
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .deleteCookies("JSESSIONID")
            .permitAll()
        );

    return http.build();
  }
  @Bean
  public AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler() {
    SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
    handler.setDefaultTargetUrl("/");
    return handler;
  }

  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails adminUser = User.builder()
        .username("admin")
        .password(passwordEncoder().encode("admin"))
        .roles("ADMIN")
        .build();

    System.out.println("Created in-memory user: " + adminUser.getUsername());
    System.out.println("Roles: " + adminUser.getAuthorities());

    return new InMemoryUserDetailsManager(adminUser);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}