package com.doktuhparadox.cryptjournal.core;

import com.doktuhparadox.easel.io.FileProprietor;
import com.doktuhparadox.easel.io.TempFile;
import com.doktuhparadox.easel.utils.StringUtils;

import org.controlsfx.dialog.Dialogs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.doktuhparadox.cryptjournal.option.OptionsManager.optionHandler;

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
		this.name = name.endsWith(".journal") ? StringUtils.strip(name, ".journal") : name;

		this.entryFileProprietor = new FileProprietor(this.getFile());
		this.entryMetadataFileProprietor = new FileProprietor(this.getMetadataFile());
	}

	public void write(String data, String password) {
		this.entryFileProprietor.write(Cryptor.en(this.fetchProperty("ENCRYPTION"), data, password), true);
	}

	public String read(String password) {
		return Cryptor.de(this.fetchProperty("ENCRYPTION"), StringUtils.collect(this.entryFileProprietor.read()), password);
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
		boolean success = FileProprietor.poll(this.getFile()) && FileProprietor.poll(this.getMetadataFile());

		if (success) {
			String timeFormat = optionHandler.get("time_format");
			this.writeProperty("CREATION", new SimpleDateFormat(String.format("%s|%s", optionHandler.get("date_format").replace("mm", "MM"), timeFormat)).format(new Date()));
			this.writeProperty("ENCRYPTION", optionHandler.get("encryption_algorithm"));
		}

		return success;
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
				if (key.equals(StringUtils.strip(pair[0], "$"))) return pair[1];
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
				if (StringUtils.strip(arr[0], "$").equals(key))
					metadataTempFile.proprietor.appendf("%s=%s", true, key, value);
				else metadataTempFile.proprietor.append(s, true);
			}

			metadataTempFile.assumeParent();
		}
	}
}
