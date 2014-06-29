package com.doktuhparadox.cryptjournal;

import com.doktuhparadox.easel.io.ReadWriter;

import java.io.File;
import java.io.IOException;
import java.security.Key;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Created and written with IntelliJ IDEA.
 * Module of: CryptJournal
 * User: brennanforrest
 * Date of creation: 6/27/14, at 12:21 PM.
 */
public class JournalEntry {

    private String name;
    private ReadWriter readWriter;

    public JournalEntry(String name) {
        this.name = name;
        this.readWriter = new ReadWriter(this.getFile());
        try {
            if (!this.getFile().exists() && this.getFile().createNewFile())
                System.out.println("Created new journal entry: " + this.name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String data, String password) {
        byte[] encodedStringBytes = null;

        try {
            Key key = this.generateKey(password);
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, key);
            encodedStringBytes = c.doFinal(data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.readWriter.write(new BASE64Encoder().encode(encodedStringBytes), false);
    }

    public String read(String password) {
        StringBuilder builder = new StringBuilder();

        for (String s : this.readWriter.read())
            builder.append(s);

        byte[] decodedStringBytes = null;

        try {
            Key key = this.generateKey(password);
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedValue = new BASE64Decoder().decodeBuffer(builder.toString());
            decodedStringBytes = c.doFinal(decodedValue);
        } catch (BadPaddingException e) {
            return "BAD_PASSWORD";
        } catch (Exception e) {
            e.printStackTrace();
        }

        //noinspection ConstantConditions
        return new String(decodedStringBytes);
    }

    public File getFile() {
        return new File("Journals/" + this.name + ".journal");
    }

    public String getName() {
        return this.name;
    }

    public void attemptDelete() {
        if (this.getFile().delete())
            System.out.println("Deleted journal entry " + this.name);
        else
            System.out.println("Could not delete entry " + this.name);
    }

    private Key generateKey(String password) {
        return new SecretKeySpec(password.getBytes(), "AES");
    }
}
