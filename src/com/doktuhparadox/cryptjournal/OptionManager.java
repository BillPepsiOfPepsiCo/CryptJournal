package com.doktuhparadox.cryptjournal;

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
    public static File configFile = new File("Options.txt");
    public static OptionHandler optionHandler = new OptionHandler(configFile);

    public static Option THEME;
    public static Option DATE_FORMAT;
    public static Option TIME_FORMAT;
    public static Option ENCRYPTION_ALGORITHM;
    public static Option TWENTY_FOUR_HOUR_TIME;

    public static void initialize() {
        THEME = new Option(optionHandler, "theme", "light");
        DATE_FORMAT = new Option(optionHandler, "date_format", "m/d/y");
        TIME_FORMAT = new Option(optionHandler, "time_format", "h:m:s");
        ENCRYPTION_ALGORITHM = new Option(optionHandler, "algorithm", "AES");
        TWENTY_FOUR_HOUR_TIME = new Option(optionHandler, "twelve_hour_time", "true");
    }
}
