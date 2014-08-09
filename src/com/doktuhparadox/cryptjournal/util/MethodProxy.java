package com.doktuhparadox.cryptjournal.util;

import com.doktuhparadox.easel.platform.PlatformDifferentiator;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.text.StrongTextEncryptor;

import com.apple.eawt.Application;

/**
 * Created and written with IntelliJ IDEA CE 14.
 * Package: com.doktuhparadox.cryptjournal.core
 * Module of: CryptJournal
 * Author: Brennan Forrest (DoktuhParadox)
 * Date of creation: 7/16/14 at 6:55 PM.
 */
public class MethodProxy {

    public static final boolean strongEncryptionAvailable = strongEncryptionAvailable();

    public static void setDockBadge(String badge) {
        if (PlatformDifferentiator.isMacOSX()) Application.getApplication().setDockIconBadge(badge);
    }

    private static boolean strongEncryptionAvailable() {
        try {
            StrongTextEncryptor ste = new StrongTextEncryptor();
            ste.setPassword("Donglord");
            ste.encrypt("dongerlord");
        } catch (EncryptionOperationNotPossibleException e) {
            if (e.getMessage().contains(" (JCE) ")) {
                return false;
            }

            e.printStackTrace();
        }

        return true;
    }
}
