package google.demo.src.account.social.service.social;

import google.demo.src.account.social.SocialOauth;
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