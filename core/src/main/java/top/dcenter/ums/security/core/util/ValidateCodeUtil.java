package top.dcenter.ums.security.core.util;

import org.springframework.lang.NonNull;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 验证码工具
 * @author zyw
 * @version V1.0  Created by 2020/5/4 9:25
 */
public class ValidateCodeUtil {

    private final static byte[] DIGITS = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z'
    };

    /**
     * 不带 - 的 uuid
     * @return  不带 - 的 uuid 字符串
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    /**
     * 使用默认字符源(2-9a-zA-Z)生成验证码
     *
     * @param verifySize 验证码长度, not null
     * @return  验证码字符串
     */
    public static String generateVerifyCode(@NonNull Integer verifySize) {
        return generateVerifyCode(verifySize, false);
    }

    /**
     * 使用数字(0-9)生成验证码
     *
     * @param verifySize 验证码长度, not null
     * @return  验证码字符串
     */
    public static String generateNumberVerifyCode(@NonNull Integer verifySize) {
        return generateVerifyCode(verifySize, true);
    }

    /**
     * 使用指定源生成验证码
     *
     * @param verifySize    验证码长度
     * @param isNumber      是否数字验证码, 用于非数字验证码时去除 0 和 1 两个数字
     * @return  验证码字符串
     */
    private static String generateVerifyCode(int verifySize, boolean isNumber) {
        Random rand = ThreadLocalRandom.current();
        int length = DIGITS.length;
        StringBuilder verifyCode = new StringBuilder(verifySize);
        for (int i = 0; i < verifySize; i++)
        {
            if (isNumber)
            {
                // 纯数字
                verifyCode.append((char) DIGITS[rand.nextInt(10)]);
            }
            else
            {
                // 去除 0 和 1 两个数字
                verifyCode.append((char) DIGITS[Math.abs(rand.nextInt(length - 2) + 2)]);
            }
        }
        return verifyCode.toString();
    }


}
