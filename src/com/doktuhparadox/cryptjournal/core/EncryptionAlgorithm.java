package com.doktuhparadox.cryptjournal.core;

/**
 * Created and written with IntelliJ IDEA CE 14.
 * Package: com.doktuhparadox.cryptjournal.core
 * Module of: CryptJournal
 * Author: Brennan Forrest (DoktuhParadox)
 * Date of creation: 7/16/14 at 7:55 PM.
 */
public enum EncryptionAlgorithm {

	AES(16),
	Blowfish(16),
	DES(8);

	public final int keyLength;

	EncryptionAlgorithm(int keyLength) {
		this.keyLength = keyLength;
	}
}
