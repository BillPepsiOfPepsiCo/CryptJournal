package com.doktuhparadox.cryptjournal.core;

import com.doktuhparadox.easel.io.FileProprietor;
import com.doktuhparadox.easel.io.TempFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.doktuhparadox.cryptjournal.core.option.OptionsManager.optionHandler;

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
	private final FileProprietor entryFileProprietor, entryMetadataFileProprietor;

	public JournalEntry(String name) {
		this.name = name.endsWith(".journal") ? name.replace(".journal", "") : name;

		this.entryFileProprietor = new FileProprietor(this.getFile());
		this.entryMetadataFileProprietor = new FileProprietor(this.getMetadataFile());

		String timeFormat = optionHandler.get("time_format");

		this.writeProperty("CREATION", new SimpleDateFormat(String.format("%s|%s", optionHandler.get("date_format").replace("mm", "MM"), timeFormat)).format(new Date()));
		this.writeProperty("ENCRYPTION", optionHandler.get("encryption_algorithm"));
	}

	public void write(String data, String password) {
		this.entryFileProprietor.write(Cryptor.en(this.fetchProperty("ENCRYPTION"), data, password), true);
	}

	public String read(String password) {
		StringBuilder builder = new StringBuilder();

		for (String s : this.entryFileProprietor.read()) {
			builder.append(s);
		}

		return Cryptor.de(this.fetchProperty("ENCRYPTION"), builder.toString(), password);
	}

	public File getFile() {
		return new File(String.format("%s%s.journal", journalDirName, this.name));
	}

	public File getMetadataFile() {
		return new File(String.format("%s%s%s.journalmetadata", journalDirName, infoDirName, this.name));
	}

	public String getName() {
		return this.name;
	}

	public boolean create() throws IOException {
		return FileProprietor.poll(this.getFile());
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
				if (key.equals(pair[0].replace("$", ""))) return pair[1];
			}
		}

		return null;
	}

	public void writeProperty(String key, String value) {
		if (this.fetchProperty(key) == null) {
			this.entryMetadataFileProprietor.appendf("$%s=%s", true, key, value);
		}
	}

	public void setProperty(String key, String value) {
		if (this.fetchProperty(key) != null) {
			TempFile metadataTempFile = new TempFile(this.getMetadataFile());

			for (String s : this.entryMetadataFileProprietor.read()) {
				String[] arr = s.split("=");
				if (arr[0].replace("$", "").equals(key)) metadataTempFile.proprietor.appendf("%s=%s", true, key, value);
				else metadataTempFile.proprietor.append(s, true);
			}

			metadataTempFile.assumeParent();
		}
	}
}
