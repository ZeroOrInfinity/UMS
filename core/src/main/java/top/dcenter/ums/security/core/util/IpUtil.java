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
package top.dcenter.ums.security.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 获取客户端的IP地址
 * @author author east7
 * @author YongWu zheng
 * @version V2.0  Created by 2020/11/1 17:49
 */
@Slf4j
public class IpUtil {

    @SuppressWarnings({"AlibabaUndefineMagicConstant"})
    public static String getRealIp(HttpServletRequest request) {
        // 获取请求主机 IP 地址, 如果通过代理进来，则透过防火墙获取真实IP地址
        String headerName = "x-forwarded-for";
        String ip = request.getHeader(headerName);
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个IP才是真实IP,它们按照英文逗号','分割
            return ip.split(",", 2)[0];
        }
        headerName = "Proxy-Client-IP";
        ip = request.getHeader(headerName);
        if (checkIp(ip)) {
            return ip;
        } else {
            headerName = "WL-Proxy-Client-IP";
            ip = request.getHeader(headerName);
        }
        if (checkIp(ip)) {
            return ip;
        } else {
            headerName = "HTTP_CLIENT_IP";
            ip = request.getHeader(headerName);
        }
        if (checkIp(ip)) {
            return ip;
        } else {
            headerName = "HTTP_X_FORWARDED_FOR";
            ip = request.getHeader(headerName);
        }
        if (checkIp(ip)) {
            return ip;
        } else {
            headerName = "X-Real-IP";
            ip = request.getHeader(headerName);
        }
        if (checkIp(ip)) {
            return ip;
        } else {
            headerName = "remote addr";
            ip = request.getRemoteAddr();
            // 127.0.0.1 ipv4, 0:0:0:0:0:0:0:1 ipv6
            if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                //根据网卡取本机配置的IP
                InetAddress inet;
                try {
                    inet = InetAddress.getLocalHost();
                    ip = inet.getHostAddress();
                }
                catch (UnknownHostException e) {
                    log.debug("从网卡取本机配置的 IP: {}, headerName: {}, 错误: {}", ip, headerName, e.getMessage());
                }
            }

        }
        return ip;
    }

    @SuppressWarnings("AlibabaUndefineMagicConstant")
    private static boolean checkIp(String ip) {
        return StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip);
    }
}
