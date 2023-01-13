package google.demo.src.account.social.controller;

import google.demo.config.Constant.SocialLoginType;
import google.demo.src.account.social.domain.GetSocialOAuthRes;
import google.demo.src.account.social.service.OAuthService;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final OAuthService oAuthService;

    /**
     * 유저 소셜 로그인으로 리다이렉트 해주는 url [GET] /accounts/auth
     *
     * @return void
     */
    @GetMapping("/auth/{socialLoginType}") //GOOGLE이 들어올 것이다.
    public void socialLoginRedirect(@PathVariable(name = "socialLoginType") String SocialLoginPath)
        throws IOException {
        log.info("하이");
        SocialLoginType socialLoginType = SocialLoginType.valueOf(SocialLoginPath.toUpperCase());
        oAuthService.request(socialLoginType);
    }

    /**
     * Social Login API Server 요청에 의한 callback 을 처리
     * @param socialLoginPath (GOOGLE, FACEBOOK, NAVER, KAKAO)
     * @param code API Server 로부터 넘어오는 code
     * @return SNS Login 요청 결과로 받은 Json 형태의 java 객체 (access_token, jwt_token, user_num 등)
     */

    @ResponseBody
    @GetMapping(value = "/auth/{socialLoginType}/callback")
    public BaseResponse<GetSocialOAuthRes> callback (
        @PathVariable(name = "socialLoginType") String socialLoginPath,
        @RequestParam(name = "code") String code)throws IOException,BaseException{
        System.out.println(">> 소셜 로그인 API 서버로부터 받은 code :"+ code);
        SocialLoginType socialLoginType= SocialLoginType.valueOf(socialLoginPath.toUpperCase());
        GetSocialOAuthRes getSocialOAuthRes=oAuthService.oAuthLogin(socialLoginType,code);
        return new BaseResponse<>(getSocialOAuthRes);
    }


}
