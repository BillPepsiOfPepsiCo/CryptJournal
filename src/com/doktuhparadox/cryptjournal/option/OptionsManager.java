package com.doktuhparadox.cryptjournal.option;

import com.doktuhparadox.easel.options.SimpleOptionsHandler;

import java.io.File;

/**
 * Created and written with IntelliJ IDEA.
 * Module of: CryptJournal
 * User: brennanforrest
 * Date of creation: 6/28/14, at 8:38 PM.
 */
public class OptionsManager {
    private static final File configFile = new File("Options.txt");
	public static final SimpleOptionsHandler optionHandler = new SimpleOptionsHandler(configFile);

    public static void initialize() {
	    optionHandler.add("theme", "light");
	    optionHandler.add("date_format", "dd/mm/yyyy");
	    optionHandler.add("time_format", "hh:mm:ss");
	    optionHandler.add("encryption_algorithm", "AES");
	    optionHandler.add("cache_passwords", "false");
	    optionHandler.add("autosave_interval", "60");
    }
}
