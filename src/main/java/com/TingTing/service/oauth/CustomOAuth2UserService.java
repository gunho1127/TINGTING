package com.TingTing.service.oauth;

import com.TingTing.entity.User;
import com.TingTing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // google, naver, kakao
        String email = null;
        String name = null;
        String providerId = null;

        String nameAttributeKey = null;
        Map<String, Object> attributes = null;

        if ("google".equals(provider)) {
            email = oAuth2User.getAttribute("email");
            name = oAuth2User.getAttribute("name");
            providerId = oAuth2User.getAttribute("sub");
            nameAttributeKey = "sub";
            attributes = oAuth2User.getAttributes();

        } else if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttributes().get("response");
            email = (String) response.get("email");
            name = (String) response.get("name");
            providerId = (String) response.get("id");
            attributes = response;
            nameAttributeKey = "id";

        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 로그인 공급자입니다: " + provider);
        }

        if (email == null || email.isEmpty()) {
            throw new OAuth2AuthenticationException("이메일 정보가 없습니다.");
        }

        Optional<User> existingUser = userRepository.findByUsEmail(email);
        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            user = userRepository.save(
                    User.builder()
                            .usEmail(email)
                            .usPw("social")
                            .usNickname(name)
                            .usProvider(provider)
                            .usProviderId(providerId)
                            .build()
            );
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                nameAttributeKey // Google은 "sub", Naver는 "id" → 어차피 attributes 그대로 쓰는 거면 key 통일 안 해도 됨
        );
    }
}
