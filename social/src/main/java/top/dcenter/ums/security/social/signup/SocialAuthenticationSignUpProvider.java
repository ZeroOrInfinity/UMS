/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package top.dcenter.ums.security.social.signup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.util.Assert;
import top.dcenter.ums.security.core.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.exception.RegisterUserFailureException;
import top.dcenter.ums.security.social.api.service.UmsSocialUserDetailsService;

/**
 * social 第三方授权登录注册 Provider, 基于 {@link DaoAuthenticationProvider} 功能的扩展
 * @see DaoAuthenticationProvider
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/6/12 22:51
 */
@Slf4j
public class SocialAuthenticationSignUpProvider implements AuthenticationProvider, InitializingBean, MessageSourceAware {

    /**
     * The plaintext password used to perform
     * PasswordEncoder#matches(CharSequence, String)}  on when the user is
     * not found to avoid SEC-2056.
     */
    private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";


    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private UserCache userCache = new NullUserCache();
    private boolean forcePrincipalAsString = false;
    protected boolean hideUserNotFoundExceptions = true;
    private UserDetailsChecker preAuthenticationChecks = new DefaultPreAuthenticationChecks();
    private UserDetailsChecker postAuthenticationChecks = new DefaultPostAuthenticationChecks();
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    private final ProviderSignInUtils providerSignInUtils;
    private UmsSocialUserDetailsService userDetailsService;

    private PasswordEncoder passwordEncoder;

    /**
     * The password used to perform
     * {@link PasswordEncoder#matches(CharSequence, String)} on when the user is
     * not found to avoid SEC-2056. This is necessary, because some
     * {@link PasswordEncoder} implementations will short circuit if the password is not
     * in a valid format.
     */
    private volatile String userNotFoundEncodedPassword;

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private UserDetailsPasswordService userDetailsPasswordService;


    public SocialAuthenticationSignUpProvider(UmsSocialUserDetailsService userDetailsService, ProviderSignInUtils providerSignInUtils, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.providerSignInUtils = providerSignInUtils;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.userCache, "A user cache must be set");
        Assert.notNull(this.messages, "A message source must be set");
        Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
    }



    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(SocialAuthenticationSignUpToken.class, authentication,
                            () -> messages.getMessage(
                                    "SocialAuthenticationSignUpProvider.onlySupports",
                                    "Only SocialAuthenticationSignUpToken is supported"));

        // Determine username
        String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED"
                : authentication.getName();

        boolean cacheWasUsed = true;
        UserDetails user = this.userCache.getUserFromCache(username);

        if (user == null) {
            cacheWasUsed = false;

            try {
                user = retrieveUser(username,
                                    (SocialAuthenticationSignUpToken) authentication);
            }
            catch (UsernameNotFoundException notFound) {
                log.debug("User '" + username + "' not found");

                if (hideUserNotFoundExceptions) {
                    throw new BadCredentialsException(messages.getMessage(
                            "AbstractUserDetailsAuthenticationProvider.badCredentials",
                            "Bad credentials"));
                }
                else {
                    throw notFound;
                }
            }

            Assert.notNull(user,
                           "retrieveUser returned null - a violation of the interface contract");
        }

        try {
            preAuthenticationChecks.check(user);
            additionalAuthenticationChecks(user,
                                           (SocialAuthenticationSignUpToken) authentication);
        }
        catch (AuthenticationException exception) {
            if (cacheWasUsed) {
                // There was a problem, so try again after checking
                // we're using latest data (i.e. not from the cache)
                cacheWasUsed = false;
                user = retrieveUser(username,
                                    (SocialAuthenticationSignUpToken) authentication);
                preAuthenticationChecks.check(user);
                additionalAuthenticationChecks(user,
                                               (SocialAuthenticationSignUpToken) authentication);
            }
            else {
                throw exception;
            }
        }

        postAuthenticationChecks.check(user);

        if (!cacheWasUsed) {
            this.userCache.putUserInCache(user);
        }

        Object principalToReturn = user;

        if (forcePrincipalAsString) {
            principalToReturn = user.getUsername();
        }

        return createSuccessAuthentication(principalToReturn, authentication, user);
    }

    protected void additionalAuthenticationChecks(UserDetails userDetails, SocialAuthenticationSignUpToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            log.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }

        String presentedPassword = authentication.getCredentials().toString();

        if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            log.debug("Authentication failed: password does not match stored value");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }

    }

    protected UserDetails retrieveUser(String username, SocialAuthenticationSignUpToken authentication) throws AuthenticationException {
        prepareTimingAttackProtection();
        try {

            UserDetails user;
            try {
                user = userDetailsService.loadUserByUserId((String) authentication.getPrincipal());
            }
            catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RegisterUserFailureException(ErrorCodeEnum.QUERY_USER_INFO_ERROR, e, authentication.getName());
            }

            if (user == null)
            {
                user = userDetailsService.registerUser(authentication.getRequest(), providerSignInUtils);
            }

            if (user == null) {
                throw new InternalAuthenticationServiceException(
                        "UserDetailsService returned null, which is an interface contract violation");
            }

            return user;

        }
        catch (UsernameNotFoundException ex) {
            mitigateAgainstTimingAttack(authentication);
            throw ex;
        }
        catch (RegisterUserFailureException ex) {
            throw ex;
        }
        catch (InternalAuthenticationServiceException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    protected Authentication createSuccessAuthentication(Object principal,
                                                         Authentication authentication, UserDetails user) {
        boolean upgradeEncoding = this.userDetailsPasswordService != null
                && this.passwordEncoder.upgradeEncoding(user.getPassword());
        if (upgradeEncoding) {
            String presentedPassword = authentication.getCredentials().toString();
            String newPassword = this.passwordEncoder.encode(presentedPassword);
            user = this.userDetailsPasswordService.updatePassword(user, newPassword);
        }

        // Ensure we return the original credentials the user supplied,
        // so subsequent attempts are successful even with encoded passwords.
        // Also ensure we return the original getDetails(), so that future
        // authentication events after cache expiry contain the details
        SocialAuthenticationSignUpToken result = new SocialAuthenticationSignUpToken(
                principal, authentication.getCredentials(),
                authoritiesMapper.mapAuthorities(user.getAuthorities()));
        result.setDetails(authentication.getDetails());

        return result;
    }

    private void prepareTimingAttackProtection() {
        if (this.userNotFoundEncodedPassword == null) {
            this.userNotFoundEncodedPassword = this.passwordEncoder.encode(USER_NOT_FOUND_PASSWORD);
        }
    }

    private void mitigateAgainstTimingAttack(SocialAuthenticationSignUpToken authentication) {
        if (authentication.getCredentials() != null) {
            String presentedPassword = authentication.getCredentials().toString();
            this.passwordEncoder.matches(presentedPassword, this.userNotFoundEncodedPassword);
        }
    }

    /**
     * Sets the PasswordEncoder instance to be used to encode and validate passwords. If
     * not set, the password will be compared using {@link PasswordEncoderFactories#createDelegatingPasswordEncoder()}
     *
     * @param passwordEncoder must be an instance of one of the {@code PasswordEncoder}
     * types.
     */
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
        this.passwordEncoder = passwordEncoder;
        this.userNotFoundEncodedPassword = null;
    }


    public UserCache getUserCache() {
        return userCache;
    }

    public boolean isForcePrincipalAsString() {
        return forcePrincipalAsString;
    }

    public boolean isHideUserNotFoundExceptions() {
        return hideUserNotFoundExceptions;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SocialAuthenticationSignUpToken.class.isAssignableFrom(authentication);
    }

    public void setForcePrincipalAsString(boolean forcePrincipalAsString) {
        this.forcePrincipalAsString = forcePrincipalAsString;
    }

    /**
     * By default the <code>AbstractUserDetailsAuthenticationProvider</code> throws a
     * <code>BadCredentialsException</code> if a username is not found or the password is
     * incorrect. Setting this property to <code>false</code> will cause
     * <code>UsernameNotFoundException</code>s to be thrown instead for the former. Note
     * this is considered less secure than throwing <code>BadCredentialsException</code>
     * for both exceptions.
     *
     * @param hideUserNotFoundExceptions set to <code>false</code> if you wish
     * <code>UsernameNotFoundException</code>s to be thrown instead of the non-specific
     * <code>BadCredentialsException</code> (defaults to <code>true</code>)
     */
    public void setHideUserNotFoundExceptions(boolean hideUserNotFoundExceptions) {
        this.hideUserNotFoundExceptions = hideUserNotFoundExceptions;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public void setUserCache(UserCache userCache) {
        this.userCache = userCache;
    }


    protected PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setUserDetailsService(UmsSocialUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    protected UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsPasswordService(
            UserDetailsPasswordService userDetailsPasswordService) {
        this.userDetailsPasswordService = userDetailsPasswordService;
    }

    protected UserDetailsChecker getPreAuthenticationChecks() {
        return preAuthenticationChecks;
    }

    /**
     * Sets the policy will be used to verify the status of the loaded
     * <tt>UserDetails</tt> <em>before</em> validation of the credentials takes place.
     *
     * @param preAuthenticationChecks strategy to be invoked prior to authentication.
     */
    public void setPreAuthenticationChecks(UserDetailsChecker preAuthenticationChecks) {
        this.preAuthenticationChecks = preAuthenticationChecks;
    }

    protected UserDetailsChecker getPostAuthenticationChecks() {
        return postAuthenticationChecks;
    }

    public void setPostAuthenticationChecks(UserDetailsChecker postAuthenticationChecks) {
        this.postAuthenticationChecks = postAuthenticationChecks;
    }

    public void setAuthoritiesMapper(GrantedAuthoritiesMapper authoritiesMapper) {
        this.authoritiesMapper = authoritiesMapper;
    }

    private class DefaultPreAuthenticationChecks implements UserDetailsChecker {
        @Override
        public void check(UserDetails user) {
            if (!user.isAccountNonLocked()) {
                log.debug("User account is locked");

                throw new LockedException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.locked",
                        "User account is locked"));
            }

            if (!user.isEnabled()) {
                log.debug("User account is disabled");

                throw new DisabledException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.disabled",
                        "User is disabled"));
            }

            if (!user.isAccountNonExpired()) {
                log.debug("User account is expired");

                throw new AccountExpiredException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.expired",
                        "User account has expired"));
            }
        }
    }

    private class DefaultPostAuthenticationChecks implements UserDetailsChecker {
        @Override
        public void check(UserDetails user) {
            if (!user.isCredentialsNonExpired()) {
                log.debug("User account credentials have expired");

                throw new CredentialsExpiredException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.credentialsExpired",
                        "User credentials have expired"));
            }
        }
    }

}