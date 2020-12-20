/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.dcenter.ums.security.jwt.factory;

import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.RSAPublicKeySpec;

/**
 * RSA
 * @author Dave Syer
 * @author Tim Ysewyn
 *
 */
@SuppressWarnings("ALL")
public class KeyStoreKeyFactory {

	private final Resource resource;
	private final char[] password;
	private KeyStore store;
	private final Object lock = new Object();
	private final String type;

	public KeyStoreKeyFactory(Resource resource, char[] password) {
		this(resource, password, type(resource));
	}

	private static String type(Resource resource) {
		String ext = StringUtils.getFilenameExtension(resource.getFilename());
		return ext == null ? "jks" : ext;
	}

	public KeyStoreKeyFactory(Resource resource, char[] password, String type) {
		this.resource = resource;
		this.password = password;
		this.type = type;
	}

	public KeyPair getKeyPair(String alias) {
		return getKeyPair(alias, password);
	}

	public KeyPair getKeyPair(String alias, char[] password) {
		try {
			synchronized (lock) {
				if (store == null) {
					synchronized (lock) {
						store = KeyStore.getInstance(type);
						InputStream stream = resource.getInputStream();
						try {
							store.load(stream, this.password);
						}
						finally {
							if (stream != null) {
								stream.close();
							}
						}
					}
				}
			}
			RSAPrivateCrtKey key = (RSAPrivateCrtKey) store.getKey(alias, password);
			Certificate certificate = store.getCertificate(alias);
			PublicKey publicKey = null;
			if (certificate != null) {
				publicKey = certificate.getPublicKey();
			} else if (key != null) {
				RSAPublicKeySpec spec = new RSAPublicKeySpec(key.getModulus(),
						key.getPublicExponent());
				publicKey = KeyFactory.getInstance("RSA").generatePublic(spec);
			}
			return new KeyPair(publicKey, key);
		}
		catch (Exception e) {
			throw new IllegalStateException("Cannot load keys from store: " + resource,
					e);
		}
	}

}
