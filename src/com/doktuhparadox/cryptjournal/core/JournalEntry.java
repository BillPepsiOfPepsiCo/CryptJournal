package com.doktuhparadox.cryptjournal.core;

import com.doktuhparadox.easel.io.FileProprietor;

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
	private final FileProprietor entryFileProprietor, entryInfoFileProprietor;

	public JournalEntry(String name) {
		this.name = name.endsWith(".journal") ? name.replace(".journal", "") : name;

		this.entryFileProprietor = new FileProprietor(this.getFile());
		this.entryInfoFileProprietor = new FileProprietor(this.getInfoFile());

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

	public String fetchProperty(String key) {
		for (String s : this.entryInfoFileProprietor.read()) {
			if (!s.startsWith("#") && s.startsWith("$")) {
				String[] pair = s.split("=");
				if (key.equals(pair[0].replace("$", ""))) return pair[1];
			}
		}

		return null;
	}

	public void writeProperty(String key, String value) {
		if (this.fetchProperty(key) == null) {
			this.entryInfoFileProprietor.appendf("$%s=%s", true, key, value);
		}
	}
}
