package demo.test; /**
 * @program: simple_tools
 * @description: AES加密
 * @author: Mr.chen
 * @create: 2020-03-08 22:00
 **/

import org.springframework.util.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * java使用AES加密解密 AES-128-ECB加密，与mysql数据库AES加密算法通用
 *
 * 数据库AES加密解密方式如下
 *  -- 加密
 *  SELECT to_base64(AES_ENCRYPT('password','1Ve2t5G%X0uopC81'));
 *  -- 解密
 *  SELECT AES_DECRYPT(from_base64('8G9m8VNJZctBNIyz9swKQw=='),'1Ve2t5G%X0uopC81');
 *
 * @author Joye
 *
 */
@SuppressWarnings("ALL")
public class AESEncryptUtils {

    /**
     * 加解密密钥, 外部可以
     */
    public static final String AES_DATA_SECURITY_KEY = "4%YkW!@g5LGcf9Ut";
    /**
     * 算法/加密模式/填充方式
     */
    private static final String AES_PKCS5P = "AES/ECB/PKCS5Padding";

    private static final String AES_PERSON_KEY_SECURITY_KEY = "pisnyMyZYXuCNcRd";

    /**
     * 加密
     *
     * @param str
     *            需要加密的字符串
     * @param key
     *            密钥
     * @return
     * @throws Exception
     */
    public static String encrypt(String str, String key) {
        if (StringUtils.isEmpty(key)) {
            throw new RuntimeException("key不能为空");
        }
        try {
            if (str == null) {
                return null;
            }
            // 判断Key是否为16位
            if (key.length() != 16) {
                return null;
            }
            byte[] raw = key.getBytes("UTF-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            // "算法/模式/补码方式"
            Cipher cipher = Cipher.getInstance(AES_PKCS5P);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(str.getBytes("UTF-8"));
            // 此处使用BASE64做转码功能，同时能起到2次加密的作用。
            return new BASE64Encoder().encode(encrypted);
        } catch (Exception ex) {
            return null;
        }

    }

    /**
     * 解密
     *
     * @param str 需要解密的字符串
     * @param key 密钥
     * @return
     */
    public static String decrypt(String str, String key) {
        if (StringUtils.isEmpty(key)) {
            throw new RuntimeException("key不能为空");
        }
        try {
            if (str == null) {
                return null;
            }
            // 判断Key是否为16位
            if (key.length() != 16) {
                return null;
            }
            byte[] raw = key.getBytes("UTF-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance(AES_PKCS5P);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            // 先用base64解密
            byte[] encrypted = new BASE64Decoder().decodeBuffer(str);
            try {
                byte[] original = cipher.doFinal(encrypted);
                String originalString = new String(original, "UTF-8");
                return originalString;
            } catch (Exception e) {
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 加密
     *
     * @param str 需要加密的字符串
     * @return
     * @throws Exception
     */
    public static String encrypt(String str) {
        return encrypt(str,AES_DATA_SECURITY_KEY);
    }

    /**
     * 解密
     * @param str 需要解密的字符串
     * @return
     */
    public static String decrypt(String str) {
        return decrypt(str,AES_DATA_SECURITY_KEY);
    }

    /**
     * 查询的时候对某些字段解密
     *
     * @param str
     * @return
     */
    public static String aesDecrypt(String str) {
        if (!StringUtils.hasText(str)) {
            return " ";
        }
        String sql = " AES_DECRYPT(from_base64(" + str + ")," + "'" + AES_DATA_SECURITY_KEY + "')";
        return sql;
    }

    /**
     * 对personKey加密
     *
     * @param personKey
     * @return
     */
    public static String encryptPersonKey(String personKey) {
        return AESEncryptUtils.encrypt(personKey, AES_PERSON_KEY_SECURITY_KEY);
    }

    /**
     * 对personKey解密
     *
     * @param personKey
     * @return
     */
    public static String decryptPersonKey(String personKey) {
        return AESEncryptUtils.decrypt(personKey, AES_PERSON_KEY_SECURITY_KEY);
    }
}