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
package top.dcenter.ums.security.common.utils;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 工具
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.6 18:22
 */
public class RsaUtil {

    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * 获取公钥,返回类型为 {@link PublicKey}
     *
     * @param key String PublicKey
     * @return PublicKey 返回公钥
     * @throws Exception 转换异常
     */
    public static RSAPublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = extractKey(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    /**
     * 转换私钥
     *
     * @param key String PrivateKey
     * @return PrivateKey 返回私钥
     * @throws Exception 转换异常
     */
    public static RSAPrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = extractKey(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    /**
     * 提取公钥或私钥通过 Base64 解码后的字节数组
     * @param originKey 原始公钥或私钥
     * @return  返回有效的公钥或私钥通过 Base64 解码后的字节数组
     */
    public static byte[] extractKey(String originKey) {
        String key = originKey.replaceAll("^-*BEGIN.*KEY-*", "")
                              .replaceAll("-*END.*KEY-*", "")
                              .replaceAll("\\s+", "");
        return Base64.getMimeDecoder().decode(key);
    }

    //***************************签名和验证*******************************

    /**
     * 签名
     *
     * @param data          数据
     * @param privateKeyStr 私钥
     * @return 签名
     * @throws Exception 签名异常
     */
    public static byte[] sign(byte[] data, String privateKeyStr) throws Exception {
        PrivateKey priK = getPrivateKey(privateKeyStr);
        Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
        sig.initSign(priK);
        sig.update(data);
        return sig.sign();
    }

    /**
     * 校验签名
     *
     * @param data         数据
     * @param sign         签名
     * @param publicKeyStr 公钥
     * @return 签名是否正确
     * @throws Exception 校验异常
     */
    public static boolean verify(byte[] data, byte[] sign, String publicKeyStr) throws Exception {
        PublicKey pubK = getPublicKey(publicKeyStr);
        Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
        sig.initVerify(pubK);
        sig.update(data);
        return sig.verify(sign);
    }

    //************************加密解密**************************

    /**
     * 加密
     *
     * @param plaintext    要加密的数据
     * @param publicKeyStr 公钥
     * @return 加密后的数据
     * @throws Exception 加密异常
     */
    public static byte[] encrypt(byte[] plaintext, String publicKeyStr) throws Exception {
        PublicKey publicKey = getPublicKey(publicKeyStr);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plaintext);
    }

    /**
     * 解密
     *
     * @param encrypted     加密后的数据
     * @param privateKeyStr 私钥
     * @return 解密后的数据
     * @throws Exception 解密异常
     */
    public static byte[] decrypt(byte[] encrypted, String privateKeyStr) throws Exception {
        PrivateKey privateKey = getPrivateKey(privateKeyStr);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encrypted);
    }

}
