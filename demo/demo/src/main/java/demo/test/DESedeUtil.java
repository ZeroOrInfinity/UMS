package demo.test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * 报文加解密工具(注意，本类所有方法均会进行base64解码)
 *
 * @author chx https://www.jianshu.com/p/36d270142893
 *
 */
@SuppressWarnings({"unused", "AlibabaLowerCamelCaseVariableNaming"})
public class DESedeUtil {
    /**
     * 密钥算法
     */
    private static String KEY_ALGORITHM = "DESede";
    private static String ECB_CIPHER_ALGORITHM = "DESede/ECB/PKCS5Padding";
    private static String CBC_CIPHER_ALGORITHM = "DESede/CBC/PKCS5Padding";

    /**
     * 加密(会对des和公钥进行base64解码)
     * 
     * @param src
     *            待加密数据
     * @param desKey
     *            密钥, 最少 24 位, 例如: "1cce4a92321940e99c6c870664c4be03"
     * @return byte[] 加密数据
     * @throws Exception    加密异常
     */
    public byte[] encryptECB(byte[] src, byte[] desKey) throws Exception {
        DESedeKeySpec dks = new DESedeKeySpec(desKey);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        SecretKey secureKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(ECB_CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secureKey);
        return cipher.doFinal(src);
    }

    /**
     * 解密
     * 
     * @param data
     *            待解密数据
     * @param desKey
     *            密钥, 最少 24 位, 例如: "1cce4a92321940e99c6c870664c4be03"
     * @return byte[] 解密数据
     * @throws Exception    解密异常
     */
    public byte[] decryptECB(byte[] data, byte[] desKey) throws Exception {
        DESedeKeySpec dks = new DESedeKeySpec(desKey);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        SecretKey secureKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(ECB_CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secureKey);
        return cipher.doFinal(data);
    }

    /**
     * CBC加密
     * @param key   密钥, 最少 24 位, 例如: "1cce4a92321940e99c6c870664c4be03"
     * @param keyiv IV  例如: {1, 2, 3, 4, 5, 6, 7, 8}
     * @param data  明文
     * @return Base64编码的密文
     * @throws Exception    解密异常
     */
    public static byte[] encryptCBC(byte[] key, byte[] keyiv, byte[] data) throws Exception {
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        SecretKey deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance(CBC_CIPHER_ALGORITHM);
        IvParameterSpec ips = new IvParameterSpec(keyiv);
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
        return cipher.doFinal(data);
    }

    /**
     * CBC解密
     * @param key   密钥, 最少 24 位, 例如: "1cce4a92321940e99c6c870664c4be03"
     * @param keyiv IV  例如: {1, 2, 3, 4, 5, 6, 7, 8}
     * @param data  Base64编码的密文
     * @return 明文
     * @throws Exception 解密异常
     */
    public static byte[] decryptCBC(byte[] key, byte[] keyiv, byte[] data) throws Exception {
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        SecretKey deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance(CBC_CIPHER_ALGORITHM);
        IvParameterSpec ips = new IvParameterSpec(keyiv);
        cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
        return cipher.doFinal(data);
    }

}