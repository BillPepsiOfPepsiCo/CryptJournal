package com.doktuhparadox.cryptjournal.core;

import com.doktuhparadox.cryptjournal.option.OptionManager;
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
public class JournalEntry {

	private static final String journalDirName = "Journals/", infoDirName = ".metadata/";
	public static final File journalDir = new File(journalDirName), infoDir = new File(journalDirName + infoDirName);
	private final FileProprietor entryFileProprietor, entryMetadataFileProprietor;
    private String name;

	public JournalEntry(String name) {
		this.name = StringUtils.strip(name, ".journal");

		this.entryFileProprietor = new FileProprietor(this.getFile());
		this.entryMetadataFileProprietor = new FileProprietor(this.getMetadataFile());
	}

	public void write(String data, String password) {
        String encData;

        if (OptionManager.useStrongEncryption.getValue() && MethodProxy.strongEncryptionAvailable()) {
            StrongTextEncryptor strongTextEncryptor = new StrongTextEncryptor();
            strongTextEncryptor.setPassword(password);
            encData = strongTextEncryptor.encrypt(data);
        } else {
            StandardPBEStringEncryptor stringEncryptor = new StandardPBEStringEncryptor();
            stringEncryptor.setAlgorithm("DESede/ECB/PKCS5Padding");
            stringEncryptor.setPassword(password);
            encData = stringEncryptor.encrypt(data);
        }

        this.entryFileProprietor.write(encData, true);
    }

	public String read(String password) {
        try {
            String data = StringUtils.collect(this.entryFileProprietor.read());

            if (MethodProxy.strongEncryptionAvailable()) {
                StrongTextEncryptor strongTextEncryptor = new StrongTextEncryptor();
                strongTextEncryptor.setPassword(password);
                return strongTextEncryptor.decrypt(data);
            } else {
                StandardPBEStringEncryptor stringEncryptor = new StandardPBEStringEncryptor();
                stringEncryptor.setAlgorithm("DESede/ECB/PKCS5Padding");
                stringEncryptor.setPassword(password);
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
            String timeFormat = OptionManager.timeFormat.getValue();
			this.writeProperty("CREATION", new SimpleDateFormat(String.format("%s|%s", OptionManager.dateFormat.getValue().replace("mm", "MM"), timeFormat)).format(new Date()));
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

				if (this.getFile().delete() && this.getMetadataFile().delete()) {
					this.name = newName;
					System.out.println("Renamed entry to " + newName);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Dialogs.create().masthead(null).title("Could not rename entry").message("An entry or metadata file with that name already exists.");
		}
	}

	public void delete() {
		if (this.getFile().delete() && this.getMetadataFile().delete())
			System.out.println("Deleted journal entry " + this.name);
		else
			System.out.println("Could not delete entry " + this.name);
	}

	public String fetchProperty(String key) {
		for (String s : this.entryMetadataFileProprietor.read()) {
			if (!s.startsWith("#") && s.startsWith("$")) {
				String[] pair = s.split("=");
				if (pair[0].equals("$" + key)) return pair[1];
			}
		}

		return null;
	}

    void writeProperty(String key, String value) {
        if (this.fetchProperty(key) == null) {
            this.entryMetadataFileProprietor.appendf("$%s=%s", true, key, value);
        }
    }

	public void setProperty(String key, String value) {
		if (this.fetchProperty(key) != null) {
			TempFile metadataTempFile = new TempFile(this.getMetadataFile());

			for (String s : this.entryMetadataFileProprietor.read()) {
				String[] arr = s.split("=");
				if (arr[0].equals("$" + key))
					metadataTempFile.proprietor.appendf("%s=%s", true, key, value);
				else metadataTempFile.proprietor.append(s, true);
			}

			metadataTempFile.assumeParent();
		}
	}
}
