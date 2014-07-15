package com.doktuhparadox.cryptjournal.core;

import com.doktuhparadox.easel.io.FileProprietor;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static com.doktuhparadox.cryptjournal.core.option.OptionsManager.optionHandler;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Created and written with IntelliJ IDEA.
 * Module of: CryptJournal
 * User: brennanforrest
 * Date of creation: 6/27/14, at 12:21 PM.
 */
public class JournalEntry {

	private static final String journalDirName = "Journals/", infoDirName = ".metadata/";
	public static final File journalDir = new File(journalDirName), infoDir = new File(journalDirName + infoDirName);

	private final String name;
	private final FileProprietor entryFileProprietor, entryInfoFileProprietor;

	public JournalEntry(String name) {
		this.name = name.endsWith(".journal") ? name.replace(".journal", "") : name;

		this.entryFileProprietor = new FileProprietor(this.getFile());
		this.entryInfoFileProprietor = new FileProprietor(this.getInfoFile());
	}

	public void write(String data, String password) {
		String configuredEncryptionAlgorithm = optionHandler.get("encryption_algorithm");
		this.writeProperties(); //Important to do this before the key generation

		byte[] encodedStringBytes = null;

		try {
			Key key = this.generateKey(password);
			Cipher c = Cipher.getInstance(configuredEncryptionAlgorithm);
			c.init(Cipher.ENCRYPT_MODE, key, new SecureRandom(password.getBytes()));
			encodedStringBytes = c.doFinal(data.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.entryFileProprietor.write(new BASE64Encoder().encode(encodedStringBytes), true);
	}

	public String read(String password) {
		String entryEncryptionAlgorithm = this.fetchProperty("ENCRYPTION");

		StringBuilder builder = new StringBuilder();

		for (String s : this.entryFileProprietor.read()) {
			builder.append(s);
		}

		byte[] decodedStringBytes = null;

		try {
			Key key = this.generateKey(password);
			Cipher c = Cipher.getInstance(entryEncryptionAlgorithm);
			c.init(Cipher.DECRYPT_MODE, key, new SecureRandom(password.getBytes()));
			byte[] decodedValue = new BASE64Decoder().decodeBuffer(builder.toString());
			decodedStringBytes = c.doFinal(decodedValue);
		} catch (BadPaddingException e) {
			return "BAD_PASSWORD";
		} catch (Exception e) {
			e.printStackTrace();
		}

		return decodedStringBytes == null ? null : new String(decodedStringBytes);
	}

	public File getFile() {
		return new File(String.format("%s%s.journal", journalDirName, this.name));
	}

	public File getInfoFile() {
		return new File(String.format("%s%s%s.nfo", journalDirName, infoDirName, this.name));
	}

	public String getName() {
		return this.name;
	}

	public boolean create() throws IOException {
		return FileProprietor.poll(this.getFile());
	}

	public void delete() {
		if (this.getFile().delete() && this.getInfoFile().delete())
			System.out.println("Deleted journal entry " + this.name);
		else
			System.out.println("Could not delete entry " + this.name);
	}

	private void writeProperties() {
		this.entryInfoFileProprietor.write("# !!!!!!!!!!!DO NOT EDIT ANYTHING IN THIS FILE FOR ANY REASON!!!!!!!!!!!", true);
		this.entryInfoFileProprietor.append("$ENCRYPTION=" + optionHandler.get("encryption_algorithm"), true);
	}

	private Key generateKey(String password) {
		return new SecretKeySpec(password.getBytes(), this.fetchProperty("ENCRYPTION"));
	}

	private String fetchProperty(String key) {
		for (String s : this.entryInfoFileProprietor.read()) {
			if (!s.startsWith("#") && s.startsWith("$")) {
				String[] pair = s.split("=");
				if (key.equals(pair[0].replace("$", ""))) return pair[1];
			}
		}

		return null;
	}
}
