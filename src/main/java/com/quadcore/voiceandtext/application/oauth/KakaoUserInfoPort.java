package com.quadcore.voiceandtext.application.oauth;

import com.quadcore.voiceandtext.infrastructure.oauth.KakaoUserInfoResponse;

public interface KakaoUserInfoPort {
    KakaoUserInfoResponse getUserInfoByCode(String authorizationCode);
}
