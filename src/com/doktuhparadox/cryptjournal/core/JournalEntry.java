package com.doktuhparadox.cryptjournal.core;

import com.doktuhparadox.cryptjournal.option.OptionManager;
import com.doktuhparadox.cryptjournal.util.Logger;
import com.doktuhparadox.cryptjournal.util.MethodProxy;
import com.doktuhparadox.easel.io.FileProprietor;
import com.doktuhparadox.easel.io.TempFile;
import com.doktuhparadox.easel.utils.StringUtils;

import org.controlsfx.dialog.Dialogs;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.text.StrongTextEncryptor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created and written with IntelliJ IDEA.
 * Module of: CryptJournal
 * User: brennanforrest
 * Date of creation: 6/27/14, at 12:21 PM.
 */
public final class JournalEntry {

    private static final String journalDirName = "Journals/", infoDirName = ".metadata/";
    public static final File journalDir = new File(journalDirName), infoDir = new File(journalDirName + infoDirName);
    private final FileProprietor entryFileProprietor, entryMetadataFileProprietor;
    private final String name;
    private boolean useStrongEncryption = OptionManager.useStrongEncryption.value().asBoolean();

    public JournalEntry(String name) {
        this.name = StringUtils.strip(name, ".journal");

        this.entryFileProprietor = new FileProprietor(this.getFile());
        this.entryMetadataFileProprietor = new FileProprietor(this.getMetadataFile());
    }

    public void write(String data, String password) {
        String encData;

        this.assertProperty("LAST_SAVE_WAS_AUTOSAVE", String.valueOf(password.equals("$")));

        if (MethodProxy.strongEncryptionAvailable && useStrongEncryption) {
            StrongTextEncryptor strongTextEncryptor = new StrongTextEncryptor();
            strongTextEncryptor.setPassword(password);
            encData = strongTextEncryptor.encrypt(data);
        } else {
            StandardPBEStringEncryptor stringEncryptor = new StandardPBEStringEncryptor();
            stringEncryptor.setAlgorithm("PBEWithMD5AndDES");
            stringEncryptor.setPassword(password);
            stringEncryptor.setKeyObtentionIterations(Integer.parseInt(this.fetchProperty("OBTENTION_ITERATIONS")));
            encData = stringEncryptor.encrypt(data);
        }

        this.entryFileProprietor.write(encData, true);
    }

    public String read(String password) {
        try {
            String data = StringUtils.collect(this.entryFileProprietor.read());

            if (MethodProxy.strongEncryptionAvailable && useStrongEncryption) {
                StrongTextEncryptor strongTextEncryptor = new StrongTextEncryptor();
                strongTextEncryptor.setPassword(password);
                return strongTextEncryptor.decrypt(data);
            } else {
                StandardPBEStringEncryptor stringEncryptor = new StandardPBEStringEncryptor();
                stringEncryptor.setAlgorithm("PBEWithMD5AndDES");
                stringEncryptor.setPassword(password);
                stringEncryptor.setKeyObtentionIterations(Integer.parseInt(this.fetchProperty("OBTENTION_ITERATIONS")));
                return stringEncryptor.decrypt(data);
            }
        } catch (EncryptionOperationNotPossibleException e) {
            return "BAD_PASSWORD";
        }
    }

    public File getFile() {
        return new File(String.format("%s%s.journal", journalDirName, this.name));
    }

    File getMetadataFile() {
        return new File(String.format("%s%s%s.journalmetadata", journalDirName, infoDirName, this.name));
    }

    public String getName() {
        return this.name;
    }

    public boolean create() throws IOException {
        if (FileProprietor.poll(this.getFile()) && FileProprietor.poll(this.getMetadataFile())) {
            this.entryMetadataFileProprietor.write("#DO NOT EDIT ANYTHING IN THIS FILE FOR RISK OF DEATH!!!!!!", true);
            this.writeProperty("CREATION", new SimpleDateFormat(String.format("%s %s", "dd/MM/yyyy", "hh:mm:ss")).format(new Date()));
            this.writeProperty("OBTENTION_ITERATIONS", OptionManager.keyObtentionIterations.value().asString());
            this.writeProperty("LAST_SAVE_WAS_AUTOSAVE", "false");
            this.lockMetadataFile();
            Logger.logInfo(String.format("Created new journal entry with name \'%s\'", this.name));
            return true;
        }

        return false;
    }

    public void rename(String newName) {
        File newEntryFile = new File(journalDirName + newName + ".journal"),
                newMetadataFile = new File(journalDirName + infoDirName + newName + ".journalmetadata");

        if (!newEntryFile.exists() && !newMetadataFile.exists()) {
            try {
                Files.move(this.getFile().toPath(), newEntryFile.toPath());
                Files.move(this.getMetadataFile().toPath(), newMetadataFile.toPath());

                Logger.logInfo(String.format("Renamed entry %s to %s", this.name, newName));
            } catch (IOException e) {
                Logger.logError("Unable to rename file: ".concat(e.toString()));
            }
        } else {
            Dialogs.create().masthead(null).title("Could not rename entry").message("An entry or metadata file with that name already exists.");
        }
    }

    public void delete() {
        this.unlockMetadataFile();
        if (this.getFile().delete() && this.getMetadataFile().delete())
            Logger.logInfo("Deleted journal entry ".concat(this.name));
        else
	        Logger.logInfo("Failed to delete journal entry".concat(this.name));
    }

	private void lockMetadataFile() {
		if (this.getMetadataFile().setReadOnly())
            Logger.logInfo(String.format("Metadata for entry %s was locked", this.name));
        else Logger.logInfo(String.format("Metadata for entry %s was unable to be locked", this.name));
    }

	private void unlockMetadataFile() {
		if (this.getMetadataFile().setWritable(true))
            Logger.logInfo(String.format("Metadata for entry %s was locked", this.name));
        else System.out.println("Unable to unlock metadata file");
    }

    public String fetchProperty(String key) {
        for (String s : this.entryMetadataFileProprietor.read()) {
            if (!s.startsWith("#") && s.startsWith("$")) {
                String[] pair = s.split("=");
                if (pair[0].equals('$' + key)) return pair[1];
            }
        }

        return null;
    }

    private void writeProperty(String key, String value) {
        if (this.fetchProperty(key) == null) {
            this.entryMetadataFileProprietor.appendf("$%s=%s", true, key, value);
        }
    }

    private void assertProperty(String key, String value) {
        if (this.fetchProperty(key) != null) {
            this.unlockMetadataFile();
            TempFile metadataTempFile = new TempFile(this.getMetadataFile());

            for (String s : this.entryMetadataFileProprietor.read()) {
                String[] arr = s.split("=");
                if (arr[0].equals('$' + key))
                    metadataTempFile.proprietor.appendf("$%s=%s", true, key, value);
                else metadataTempFile.proprietor.append(s, true);
            }

            metadataTempFile.assumeParent();
            this.lockMetadataFile();
        }
    }
}
