package com.example.springDemo.service;

import com.example.springDemo.model.User;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

public interface UserService {
  // 基本用户管理方法
  User saveUser(User user);
  Optional<User> findByUsername(String username);
  boolean existsByUsername(String username);
  Optional<User> findByEmail(String email);

  // OAuth2相关方法
  OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException;
  User processOAuth2User(String provider, java.util.Map<String, Object> attributes);
}