package demo.controller;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.common.vo.ResponseResult;
import top.dcenter.ums.security.core.oauth.properties.Auth2Properties;
import top.dcenter.ums.security.core.vo.AuthTokenVo;
import top.dcenter.ums.security.properties.UmsProperties;

import java.nio.charset.StandardCharsets;

import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.hasText;

/**
 * 第三方登录后获取 token 的控制器
 *
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.1.5 11:54
 */
@Controller
public class Oauth2TokenHandlerController {

    private final String domain;
    private final RedisConnectionFactory redisConnectionFactory;
    private final UmsProperties umsProperties;

    public Oauth2TokenHandlerController(Auth2Properties auth2Properties,
                                        RedisConnectionFactory redisConnectionFactory,
                                        UmsProperties umsProperties) {
        this.domain = auth2Properties.getDomain();
        this.redisConnectionFactory = redisConnectionFactory;
        this.umsProperties = umsProperties;
    }

    @RequestMapping(value = "/login", method = {RequestMethod.GET})
    public String login() {
        return "login";
    }


    @RequestMapping(value = "/oauth2Token", method = {RequestMethod.GET})
    public String auth2Token(@RequestParam("tk") String tk,
                             @RequestParam("username") String username,
                             @RequestParam("id") String id, Model model) {
        model.addAttribute(umsProperties.getOauth2TokenParamName(), tk);
        model.addAttribute("username", username);
        model.addAttribute("id", id);
        return "oauth2Token";
    }

    @RequestMapping(value = "/oauth2Callback", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseResult oAuth2LoginSuccessCallback(@RequestParam("tk") String tk,
                                                     @RequestParam("username") String username,
                                                     @RequestParam("id") String id) {
        if (hasText(tk)) {
            byte[] bytes =
                    getConnection().get((umsProperties.getTempOauth2TokenPrefix() + tk).getBytes(StandardCharsets.UTF_8));
            if (nonNull(bytes)) {
                // tokenInfo = jwtToken#@#refreshToken#@#url 或 tokenInfo = jwtToken#@#url
                String tokenInfo = new String(bytes, StandardCharsets.UTF_8);
                if (!hasText(tokenInfo)) {
                    return ResponseResult.fail(ErrorCodeEnum.UNAUTHORIZED);
                }
                String[] split = tokenInfo.split(umsProperties.getDelimiterOfTokenAndRefreshToken());
                int length = split.length;
                AuthTokenVo authTokenVo = new AuthTokenVo();
                authTokenVo.setId(id);
                authTokenVo.setUsername(username);
                authTokenVo.setToken(split[0]);
                authTokenVo.setTargetUrl(split[length - 1]);
                if (length - 1 != 1) {
                    authTokenVo.setRefreshToken(split[1]);
                }
                return ResponseResult.success("成功获取 token", authTokenVo);
            }
        }
        return ResponseResult.fail(ErrorCodeEnum.UNAUTHORIZED);
    }

    @NonNull
    private RedisConnection getConnection() {
        return this.redisConnectionFactory.getConnection();
    }

}