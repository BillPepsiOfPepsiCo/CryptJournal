package com.doktuhparadox.cryptjournal.core;

import org.apache.commons.codec.binary.Base64;

import java.security.Key;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * Created and written with IntelliJ IDEA CE 14.
 * Package: com.doktuhparadox.cryptjournal.core
 * Module of: CryptJournal
 * Author: Brennan Forrest (DoktuhParadox)
 * Date of creation: 7/15/14 at 10:09 PM.
 */
class Cryptor {

	public static String en(String algorithm, String data, String password) {
        byte[] encodedStringBytes = null;

		try {
			Key key = generateKey(algorithm, password);
			Cipher c = Cipher.getInstance(algorithm);
            c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(c.getIV()));
            encodedStringBytes = c.doFinal(data.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}

        return new String(Base64.encodeBase64(encodedStringBytes));
    }

	public static String de(String algorithm, String data, String password) {
		byte[] decodedStringBytes = null;

		try {
			Key key = generateKey(algorithm, password);
			Cipher c = Cipher.getInstance(algorithm);
            c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(c.getIV()));
            byte[] decodedValue = Base64.decodeBase64(data.getBytes());
            decodedStringBytes = c.doFinal(decodedValue);
		} catch (BadPaddingException e) {
			return "BAD_PASSWORD";
		} catch (Exception e) {
			e.printStackTrace();
		}

		return decodedStringBytes == null ? null : new String(decodedStringBytes);
	}

	private static Key generateKey(String algorithm, String password) {
		return new SecretKeySpec(password.getBytes(), algorithm);
	}
}
