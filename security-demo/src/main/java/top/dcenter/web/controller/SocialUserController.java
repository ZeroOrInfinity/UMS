package top.dcenter.web.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.dto.User;
import top.dcenter.security.core.vo.ResponseResult;
import top.dcenter.security.social.vo.SocialUserInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 第三方登录用户控制器
 *
 * @author zhailiang
 * @version V1.0  Created by 2020/5/1 19:49
 * @medifiedBy zyw
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class SocialUserController {

    private final ProviderSignInUtils providerSignInUtils;

    public SocialUserController(ProviderSignInUtils providerSignInUtils) {
        this.providerSignInUtils = providerSignInUtils;
    }

    @GetMapping("/info")
    public SocialUserInfo getSocialUserInfo(HttpServletRequest request) {
        // 获取用户信息逻辑
        // ...
        log.info("Demo =======>: SocialUserController.getSocialUserInfo");
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(new ServletWebRequest(request));
        if (connection == null)
        {
            return null;
        }
        SocialUserInfo userInfo = new SocialUserInfo();
        userInfo.setProviderId(connection.getKey().getProviderId());
        userInfo.setProviderUserId(connection.getKey().getProviderUserId());
        userInfo.setNickname(connection.getDisplayName());
        userInfo.setHeadImg(connection.getImageUrl());
        return userInfo;
    }

    @PostMapping("/regist")
    public ResponseResult regist(User user, HttpServletRequest request, HttpServletResponse response) {
        // 注册用户逻辑
        // ...
        log.info("Demo ========>: SocialUserController.regist");
        // 不管是注册用户还是绑定用户，都会拿到一个用户唯一标识，
        if (providerSignInUtils == null)
        {
            return ResponseResult.fail(500, "服务不存在");
        }
        String userId = user.getUsername();
        try
        {
            providerSignInUtils.doPostSignUp(userId, new ServletWebRequest(request));
        }
        catch (DuplicateConnectionException e)
        {
            log.info("用户注册失败：{}", user);
            return ResponseResult.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "用户注册失败");
        }

        log.info("Demo ========>: 用户注册成功：{}", user);
        return ResponseResult.success(user);
    }

    @GetMapping("/testWebSecurityPostConfigurer")
    public ResponseResult testWebSecurityPostConfigurer(HttpServletRequest request) {
        log.info("Demo ========>: SocialUserController.testWebSecurityPostConfigurer");
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(new ServletWebRequest(request));
        if (connection == null)
        {
            return ResponseResult.fail(HttpStatus.NON_AUTHORITATIVE_INFORMATION.value(), "没有第三方授权信息");
        }
        SocialUserInfo userInfo = new SocialUserInfo();
        userInfo.setProviderId(connection.getKey().getProviderId());
        userInfo.setProviderUserId(connection.getKey().getProviderUserId());
        userInfo.setNickname(connection.getDisplayName());
        userInfo.setHeadImg(connection.getImageUrl());
        log.info("用户注册成功：{}", userInfo);
        return ResponseResult.success(userInfo);
    }

    @GetMapping("/me")
    public Object getCurrentUser(@AuthenticationPrincipal UserDetails userDetails, Authentication authentication) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("authenticationHolder", SecurityContextHolder.getContext().getAuthentication());
        map.put("userDetails", userDetails);
        map.put("authentication", authentication);
        return map;
    }


    @GetMapping(value = "")
    @JsonView(User.UserSimpleView.class)
    public List<User> listUsers(
            @RequestParam(value = "username", required = false) String username,
            @PageableDefault(page = 5, size = 20, sort = "username,asc") Pageable pageable) {
        if (log.isDebugEnabled())
        {
            log.debug("listUsers: " + username);
            log.debug("listUsers: size=" + pageable.getPageSize() + "; page="
                              + pageable.getPageNumber() + " sort="
                              + pageable.getSort());
        }
        List<User> users = new ArrayList<>();
        users.add(new User("jack", "1111"));
        users.add(new User("lose", "1111"));
        users.add(new User("Tom", "1111"));
        return users;
    }

    @GetMapping("/{id:\\d+}")
    @JsonView(User.UserDetailView.class)
    public User getInfo(@PathVariable("id") String id) {
        if (log.isDebugEnabled())
        {
            log.debug("getInfo: " + id);
        }
        return new User("tom", "1111");
    }

    @PostMapping("")
    @JsonView(User.UserSimpleView.class)
    public User createUser(@Valid @RequestBody() User user) {
        if (log.isDebugEnabled())
        {
            log.debug("createUser: " + user);
        }
        user.setId("1");
        return user;
    }

    @PutMapping("/{id:\\d+}")
    @JsonView(User.UserSimpleView.class)
    public User update(
            @PathVariable("id") String id,
            @Valid @RequestBody User user,
            BindingResult errors) {
        if (errors.hasErrors())
        {
            errors.getAllErrors().forEach(
                    (error) ->
                    {
                        FieldError fieldError = (FieldError) error;
                        String eMsg = fieldError.getField() + error.getDefaultMessage();
                        log.error(eMsg);
                    }
            );
        }
        if (log.isDebugEnabled())
        {
            log.debug("listUsers: id=" + id + "; " + user);
        }
        return user;
    }

    @DeleteMapping("/{id:\\d+}")
    public void delete(@PathVariable("id") String id) {
        if (log.isInfoEnabled())
        {
            log.info("delete id=" + id);
        }
    }
}
