package com.test.project.socialLogin.account.social.service.social;

import com.test.project.socialLogin.account.social.SocialOauth;
import org.springframework.stereotype.Component;

@Component
public class FacebookOauth implements SocialOauth {
    @Override
    public String getOauthRedirectURL() {
        return "";
    }

    @Override
    public String requestAccessToken(String code) {
        return null;
    }
}