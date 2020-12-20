package demo.test;

import org.springframework.core.io.FileUrlResource;
import top.dcenter.ums.security.jwt.factory.KeyStoreKeyFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Base64;

/**
 * 从jks文件中导出私钥和证书
 * @author YongWu zheng
 * @version V2.0  Created by 2020-12-06 21:06
 */
public class ExportKey {

    private File keystoreFile;
    private String keyStoreType;
    private char[] password;
    private String alias;
    private File exportedPrivateKeyFile;
    private File exportedPublicKeyFile;

    public static KeyPair getKeyPair(KeyStore keystore, String alias,char[] password) {
        try {
            Key key = keystore.getKey(alias, password);
            if (key instanceof PrivateKey) {
                Certificate cert = keystore.getCertificate(alias);
                PublicKey publicKey = cert.getPublicKey();
                return new KeyPair(publicKey, (PrivateKey) key);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public void exportPrivate() throws Exception {
        KeyStore keystore = KeyStore.getInstance(keyStoreType);
        keystore.load(new FileInputStream(keystoreFile), password);

        Base64.Encoder encoder = Base64.getMimeEncoder();
        FileUrlResource resource = new FileUrlResource(keystoreFile.getAbsolutePath());
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, password);

        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias);
        PrivateKey privateKey = keyPair.getPrivate();

        String encoded = encoder.encodeToString(privateKey.getEncoded());
        String keystoreEncoded = encoder.encodeToString(keystore.getCertificate(alias).getEncoded());

        FileWriter fw = new FileWriter(exportedPrivateKeyFile);
        fw.write("-----BEGIN PRIVATE KEY-----\n");
        fw.write(encoded);
        fw.write("\n");
        fw.write("-----END PRIVATE KEY-----");
        fw.write("\n");
        fw.write("-----BEGIN CERTIFICATE-----\n");
        fw.write(keystoreEncoded);
        fw.write("\n");
        fw.write("-----END CERTIFICATE-----");
        fw.close();
    }

    public void exportCertificate() throws Exception {
        KeyStore keystore = KeyStore.getInstance(keyStoreType);
        keystore.load(new FileInputStream(keystoreFile), password);

        Base64.Encoder encoder = Base64.getMimeEncoder();
        FileUrlResource resource = new FileUrlResource(keystoreFile.getAbsolutePath());
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, password);

        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias);

        String encoded = encoder.encodeToString(keyPair.getPublic().getEncoded());
        String keystoreEncoded = encoder.encodeToString(keystore.getCertificate(alias).getEncoded());

        FileWriter fw = new FileWriter(exportedPublicKeyFile);
        fw.write("-----BEGIN PUBLIC KEY-----\n");
        fw.write(encoded);
        fw.write("\n");
        fw.write("-----END PUBLIC KEY-----");
        fw.write("\n");
        fw.write("-----BEGIN CERTIFICATE-----\n");
        fw.write(keystoreEncoded);
        fw.write("\n");
        fw.write("-----END CERTIFICATE-----");
        fw.close();
    }

    public static void main(String args[]) throws Exception {
        ExportKey export = new ExportKey();
        export.keystoreFile = new File("D:\\Workspaces-Idea\\proc\\ums\\demo\\demo\\src\\main\\resources\\zyw.jks");
        export.keyStoreType = "JKS";
        export.password = "123456".toCharArray();
        export.alias = "zyw";
        export.exportedPrivateKeyFile = new File("D:\\Workspaces-Idea\\proc\\ums\\demo\\demo\\src\\main\\resources\\pkcs8.key");
        export.exportedPublicKeyFile = new File("D:\\Workspaces-Idea\\proc\\ums\\demo\\demo\\src\\main\\resources\\public.key");
        export.exportPrivate();
        export.exportCertificate();
    }

}