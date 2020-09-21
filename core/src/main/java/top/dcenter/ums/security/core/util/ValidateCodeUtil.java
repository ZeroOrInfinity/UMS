package top.dcenter.ums.security.core.util;

import java.util.Random;
import java.util.UUID;

/**
 * 验证码工具
 * @author zyw
 * @version V1.0  Created by 2020/5/4 9:25
 */
public class ValidateCodeUtil {

    public static final String VERIFY_CODES = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    public static final String NUMBER_VERIFY_CODES = "0123456789";


    /**
     * 不带 - 的 uuid
     * @return  不带 - 的 uuid 字符串
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    /**
     * 使用系统默认字符源生成验证码
     *
     * @param verifySize
     *            验证码长度
     * @return  验证码字符串
     */
    public static String generateVerifyCode(int verifySize) {
        return generateVerifyCode(verifySize, VERIFY_CODES);
    }

    /**
     * 使用数字生成验证码
     *
     * @param verifySize
     *            验证码长度
     * @return  验证码字符串
     */
    public static String generateNumberVerifyCode(int verifySize) {
        return generateVerifyCode(verifySize, NUMBER_VERIFY_CODES);
    }

    /**
     * 使用指定源生成验证码
     *
     * @param verifySize
     *            验证码长度
     * @param sources
     *            验证码字符源
     * @return  验证码字符串
     */
    public static String generateVerifyCode(int verifySize, String sources) {
        if (sources == null || sources.length() == 0) {
            sources = VERIFY_CODES;
        }
        int codeLen = sources.length();
        Random rand = new Random(System.currentTimeMillis());
        StringBuilder verifyCode = new StringBuilder(verifySize);
        for (int i = 0; i < verifySize; i++) {
            verifyCode.append(sources.charAt(rand.nextInt(codeLen - 1)));
        }
        return verifyCode.toString();
    }


}
