package com.bside405.perfume.project.oauth2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2Service extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2Service.class);
    private final UserRepository userRepository;

    public CustomOAuth2Service(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User;
        try {
            oAuth2User = super.loadUser(userRequest);
        } catch (OAuth2AuthenticationException e) {
            logger.error("OAuth2 사용자 로드 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }

        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        if (response != null) {
            // 새로운 OAuth2User 객체를 생성하여 사용자 정보를 response에서 추출
            oAuth2User = new DefaultOAuth2User(
                    oAuth2User.getAuthorities(),
                    response,
                    "id" // 네이버 사용자 정보에서 고유 식별자 속성 이름
            );
        } else {
            String errorMessage = "OAuth2User에서 'response' 속성을 찾을 수 없습니다.";
            logger.error(errorMessage);
            throw new OAuth2AuthenticationException(errorMessage);
        }

        processUser(oAuth2User);
        return oAuth2User;
    }

    private void processUser(OAuth2User oAuth2User) {
        Map<String, Object> response = oAuth2User.getAttributes();
        if (response != null) {
            String email = (String) response.get("email");
            String name = (String) response.get("name");
            String providerId = (String) response.get("id");

            logger.info("OAuth2 사용자 정보 - 이메일: {}, 이름: {}, 제공자 ID: {}", email, name, providerId);

            Optional<User> optionalUser = userRepository.findByProviderId(providerId);

            if (optionalUser.isEmpty()) {
                User user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setProviderId(providerId);
                userRepository.save(user);

                logger.info("새로운 사용자 저장 - 이메일: {}, 이름: {}, 제공자 ID: {}", email, name, providerId);
            } else {
                logger.info("기존 사용자 로그인 - 이메일: {}, 이름: {}, 제공자 ID: {}", email, name, providerId);
            }
        } else {
            logger.error("OAuth2User의 속성에서 사용자 정보를 찾을 수 없습니다.");
        }
    }
}
