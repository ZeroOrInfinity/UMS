package top.dcenter.ums.security.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import top.dcenter.ums.security.core.api.config.HttpSecurityAware;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeFilter;
import top.dcenter.ums.security.core.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.core.properties.ValidateCodeProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static top.dcenter.ums.security.core.bean.UriHttpMethodTuple.tuple;

/**
 * 验证码相关配置
 * @author zyw
 * @version V1.0  Created by 2020/5/15 21:59
 */
@Configuration
@AutoConfigureAfter({ValidateCodeBeanAutoConfiguration.class})
@Slf4j
public class ValidateCodeAutoConfigurerAware implements HttpSecurityAware {

    private final ValidateCodeProperties validateCodeProperties;
    private final ValidateCodeFilter validateCodeFilter;

    public ValidateCodeAutoConfigurerAware(ValidateCodeProperties validateCodeProperties,
                                           ValidateCodeFilter validateCodeFilter) {
        this.validateCodeProperties = validateCodeProperties;
        this.validateCodeFilter = validateCodeFilter;
    }

    @Override
    public void postConfigure(HttpSecurity http) {
        http.addFilterBefore(validateCodeFilter, AbstractPreAuthenticatedProcessingFilter.class);
    }

    @Override
    public void preConfigure(HttpSecurity http) {
        // dto nothing

    }

    @Override
    public Map<String, Map<UriHttpMethodTuple, Set<String>>> getAuthorizeRequestMap() {
        final Map<UriHttpMethodTuple, Set<String>> permitAllMap = new HashMap<>(16);
        ValidateCodeProperties.SliderCodeProperties slider = validateCodeProperties.getSlider();

        permitAllMap.put(tuple(GET, validateCodeProperties.getGetValidateCodeUrlPrefix() + "/**"), null);
        permitAllMap.put(tuple(POST, slider.getSliderCheckUrl()), null);

        validateCodeProperties.getSms().getAuthUrls().forEach(uri -> permitAllMap.put(tuple(POST, uri), null));
        validateCodeProperties.getImage().getAuthUrls().forEach(uri -> permitAllMap.put(tuple(POST, uri), null));
        slider.getAuthUrls().forEach(uri -> permitAllMap.put(tuple(POST, uri), null));
        validateCodeProperties.getSelection().getAuthUrls().forEach(uri -> permitAllMap.put(tuple(POST, uri), null));
        validateCodeProperties.getTrack().getAuthUrls().forEach(uri -> permitAllMap.put(tuple(POST, uri), null));
        validateCodeProperties.getCustomize().getAuthUrls().forEach(uri -> permitAllMap.put(tuple(POST, uri), null));


        Map<String, Map<UriHttpMethodTuple, Set<String>>> resultMap = new HashMap<>(1);

        resultMap.put(HttpSecurityAware.PERMIT_ALL, permitAllMap);

        return resultMap;
    }
}
