package google.demo.src.account.social.service;

import google.demo.config.Constant;
import google.demo.src.account.social.GoogleOauth;
import google.demo.src.account.social.domain.GetSocialOAuthRes;
import google.demo.src.account.social.domain.GoogleOAuthToken;
import google.demo.src.account.social.domain.GoogleUser;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final GoogleOauth googleOauth;
    private final HttpServletResponse response;

    public void request(Constant.SocialLoginType socialLoginType) throws IOException {
        String redirectURL;
        switch (socialLoginType) {
            case GOOGLE: {
                //각 소셜 로그인을 요청하면 소셜로그인 페이지로 리다이렉트 해주는 프로세스이다.
                redirectURL = googleOauth.getOauthRedirectURL();
            }
            break;
            default: {
                throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
            }

        }

        response.sendRedirect(redirectURL);
    }

    public GetSocialOAuthRes oAuthLogin(Constant.SocialLoginType socialLoginType, String code)
        throws IOException {

        switch (socialLoginType) {
            case GOOGLE: {
                //구글로 일회성 코드를 보내 액세스 토큰이 담긴 응답객체를 받아옴
                ResponseEntity<String> accessTokenResponse = googleOauth.requestAccessToken(code);
                //응답 객체가 JSON형식으로 되어 있으므로, 이를 deserialization해서 자바 객체에 담을 것이다.
                GoogleOAuthToken oAuthToken = googleOauth.getAccessToken(accessTokenResponse);

                //액세스 토큰을 다시 구글로 보내 구글에 저장된 사용자 정보가 담긴 응답 객체를 받아온다.
                ResponseEntity<String> userInfoResponse = googleOauth.requestUserInfo(oAuthToken);
                //다시 JSON 형식의 응답 객체를 자바 객체로 역직렬화한다.
                GoogleUser googleUser = googleOauth.getUserInfo(userInfoResponse);

                String user_id = googleUser.getEmail();

                //우리 서버의 db와 대조하여 해당 user가 존재하는 지 확인한다.
                int user_num = accountProvider.getUserNum(user_id);

                if (user_num != 0) {
                    //서버에 user가 존재하면 앞으로 회원 인가 처리를 위한 jwtToken을 발급한다.
                    String jwtToken = jwtService.createJwt(user_num, user_id);
                    //액세스 토큰과 jwtToken, 이외 정보들이 담긴 자바 객체를 다시 전송한다.
                    GetSocialOAuthRes getSocialOAuthRes = new GetSocialOAuthRes(jwtToken, user_num,
                        oAuthToken.getAccess_token(), oAuthToken.getToken_type());
                    return getSocialOAuthRes;
                } else {
                    throw new BaseException(BaseResponseStatus.ACCOUNT_DOESNT_EXISTS);
                }

            }
            default: {
                throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
            }

        }
    }


}