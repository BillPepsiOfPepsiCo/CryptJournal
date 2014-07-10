package com.doktuhparadox.cryptjournal.core.option;

import com.doktuhparadox.easel.options.Option;
import com.doktuhparadox.easel.options.OptionHandler;

import java.io.File;

/**
 * Created and written with IntelliJ IDEA.
 * Module of: CryptJournal
 * User: brennanforrest
 * Date of creation: 6/28/14, at 8:38 PM.
 */
public class OptionManager {
    private static final File configFile = new File("Options.txt");
    private static final OptionHandler optionHandler = new OptionHandler(configFile);

    public static Option THEME;
    public static Option DATE_FORMAT;
    public static Option TIME_FORMAT;
    public static Option ENCRYPTION_ALGORITHM;
    public static Option TWELVE_HOUR_TIME;
	public static Option CACHE_PASSWORDS;

    public static void initialize() {
        THEME = new Option(optionHandler, "theme", "light");
        DATE_FORMAT = new Option(optionHandler, "date_format", "m/d/y");
        TIME_FORMAT = new Option(optionHandler, "time_format", "h:m:s");
        ENCRYPTION_ALGORITHM = new Option(optionHandler, "algorithm", "AES");
        TWELVE_HOUR_TIME = new Option(optionHandler, "twelve_hour_time", "true");
	    CACHE_PASSWORDS = new Option(optionHandler, "cache_passwords", "true");
    }
}
