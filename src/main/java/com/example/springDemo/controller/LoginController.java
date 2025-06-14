package com.example.springDemo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

  @GetMapping("/login")
  public String login() {
    return "login";
  }

  @GetMapping("/")
  public String home(@AuthenticationPrincipal OAuth2User oauth2User, Model model) {
    if (oauth2User != null) {
      // Extract user information from OAuth2User
      String name = oauth2User.getAttribute("name");
      String email = oauth2User.getAttribute("email");

      // Add attributes to model
      model.addAttribute("name", name);
      model.addAttribute("email", email);

      // Log user information for debugging
      System.out.println("Authenticated user: " + name + " (" + email + ")");
      System.out.println("OAuth2 Attributes: " + oauth2User.getAttributes());
    } else {
      System.out.println("No authenticated user found");
    }

    return "home";
  }

  // Additional endpoint for testing authentication
  @GetMapping("/user-info")
  public String userInfo(@AuthenticationPrincipal OAuth2User oauth2User, Model model) {
    if (oauth2User != null) {
      model.addAttribute("userAttributes", oauth2User.getAttributes());
      model.addAttribute("authorities", oauth2User.getAuthorities());
      return "user-info";
    }
    return "redirect:/login";
  }
}