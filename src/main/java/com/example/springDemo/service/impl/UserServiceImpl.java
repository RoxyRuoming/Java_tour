package com.example.springDemo.service.impl;

import com.example.springDemo.model.Role;
import com.example.springDemo.model.User;
import com.example.springDemo.repository.postgres.UserRepository;
import com.example.springDemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl extends DefaultOAuth2UserService implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  // 基本用户管理实现
  @Override
  public User saveUser(User user) {
    // 确保密码被加密，如果密码不为空
    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
    }
    return userRepository.save(user);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  // OAuth2相关实现
  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oauth2User = super.loadUser(userRequest);

    // Extract provider details
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    String userNameAttributeName = userRequest.getClientRegistration()
        .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

    // Process and store user information
    User user = processOAuth2User(registrationId, oauth2User.getAttributes());

    // Create a new OAuth2User with the nameAttributeKey
    Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());

    // Log the process
    System.out.println("OAuth2 login processed for: " + user.getUsername());
    System.out.println("Provider: " + registrationId);
    System.out.println("Attribute name: " + userNameAttributeName);

    return new DefaultOAuth2User(
        oauth2User.getAuthorities(),
        attributes,
        userNameAttributeName);
  }

  @Override
  public User processOAuth2User(String provider, Map<String, Object> attributes) {
    String email;

    // GitHub特殊处理，可能需要另外获取邮箱
    if ("github".equals(provider)) {
      // GitHub可能不直接返回email，或者返回null
      email = (String) attributes.get("email");
      if (email == null || email.isEmpty()) {
        // 可以使用用户名作为替代
        String login = (String) attributes.get("login");
        email = login + "@github.com"; // 创建一个基于用户名的伪邮箱
        System.out.println("Using GitHub username as email: " + email);
      }
    } else {
      email = (String) attributes.get("email");
    }

    // 查找现有用户
    Optional<User> existingUser = userRepository.findByEmail(email);

    if (existingUser.isPresent()) {
      System.out.println("Found existing user: " + existingUser.get().getUsername());
      return updateExistingUser(existingUser.get(), provider, attributes);
    } else {
      System.out.println("Creating new user for: " + email);
      return registerNewUser(provider, attributes, email);
    }
  }

  // 修改这个方法签名
  private User registerNewUser(String provider, Map<String, Object> attributes, String email) {
    User user = new User();

    // 设置基本信息
    user.setEmail(email);

    if ("github".equals(provider)) {
      user.setUsername((String) attributes.get("login"));
      user.setName((String) attributes.get("name"));
      user.setProviderId(attributes.get("id").toString());
    } else {
      user.setUsername(email);
      user.setName((String) attributes.get("name"));
      user.setProviderId((String) attributes.get("sub"));
    }

    user.setProvider(provider);

    // 为 OAuth2 用户设置一个随机密码
    String randomPassword = UUID.randomUUID().toString();
    user.setPassword(passwordEncoder.encode(randomPassword));

    // 设置角色
    Set<Role> roles = new HashSet<>();
    roles.add(Role.ROLE_USER);
    user.setRoles(roles);

    return userRepository.save(user);
  }

  private User updateExistingUser(User user, String provider, Map<String, Object> attributes) {
    // 更新相关字段
    if ("github".equals(provider)) {
      user.setName((String) attributes.get("name"));
      user.setProviderId(attributes.get("id").toString());
    } else {
      user.setName((String) attributes.get("name"));
      user.setProviderId((String) attributes.get("sub"));
    }

    user.setProvider(provider);

    return userRepository.save(user);
  }
}