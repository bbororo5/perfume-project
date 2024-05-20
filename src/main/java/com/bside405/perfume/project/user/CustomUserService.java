package com.bside405.perfume.project.user;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomUserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        processUser(oAuth2User);
        return oAuth2User;
    }

    private void processUser(OAuth2User oAuth2User) {
        Map<String, String> response = oAuth2User.getAttribute("response");
        if (response != null) {
            String email = response.get("email");
            String name = response.get("name");
            String providerId = response.get("id");

            User user = userRepository.findByProviderId(providerId);
            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setProviderId(providerId);
                userRepository.save(user);
            }
        }
    }
}