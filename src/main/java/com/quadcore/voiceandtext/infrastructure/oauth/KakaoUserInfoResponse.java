package com.quadcore.voiceandtext.infrastructure.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfoResponse {

    @JsonProperty("id")
    private Long kakaoId;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @JsonProperty("properties")
    private Properties properties;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoAccount {
        @JsonProperty("email")
        private String email;

        @JsonProperty("email_needs_agreement")
        private Boolean emailNeedsAgreement;

        @JsonProperty("profile")
        private Profile profile;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Profile {
        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("profile_image_url")
        private String profileImageUrl;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Properties {
        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("profile_image")
        private String profileImage;
    }

    public String getEmail() {
        if (kakaoAccount == null || kakaoAccount.email == null || kakaoAccount.email.isBlank()) {
            return null;
        }
        if (kakaoAccount.emailNeedsAgreement != null && kakaoAccount.emailNeedsAgreement) {
            return null;
        }
        return kakaoAccount.email;
    }

    public String getNickname() {
        if (kakaoAccount != null && kakaoAccount.profile != null && kakaoAccount.profile.nickname != null && !kakaoAccount.profile.nickname.trim().isEmpty()) {
            return kakaoAccount.profile.nickname;
        }
        if (properties != null && properties.nickname != null && !properties.nickname.trim().isEmpty()) {
            return properties.nickname;
        }
        return null;
    }

    public String getProfileImageUrl() {
        if (kakaoAccount != null && kakaoAccount.profile != null && kakaoAccount.profile.profileImageUrl != null && !kakaoAccount.profile.profileImageUrl.trim().isEmpty()) {
            return kakaoAccount.profile.profileImageUrl;
        }
        if (properties != null && properties.profileImage != null && !properties.profileImage.trim().isEmpty()) {
            return properties.profileImage;
        }
        return null;
    }
}
