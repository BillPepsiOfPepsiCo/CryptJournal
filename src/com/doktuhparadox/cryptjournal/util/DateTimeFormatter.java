package com.doktuhparadox.cryptjournal.util;

import com.doktuhparadox.cryptjournal.core.option.OptionManager;

/**
 * Created and written with IntelliJ IDEA.
 * Module of: CryptJournal
 * User: brennanforrest
 * Date of creation: 6/28/14, at 9:18 PM.
 * <p>
 * Just a dummy class that could have some use outside of {@link com.doktuhparadox.cryptjournal.core.JournalEntryListCellFactory}.
 */
public class DateTimeFormatter {
    /**
     * Formats the passed string array into a date.
     *
     * @param yearMonthDay the array that contains the year, month, and day. The contents of the array must be ordered as follows: <br>
     *                     <code>yearMonthDay[0]</code> must be the year, <br>
     *                     <code>yearMonthDay[1]</code> must be the month, and <br>
     *                     <code>yearMonthDay[2]</code> must be the day, as the name of the array implies.
     * @return the date, formatted according to the format in the configuration file.
     */
    public static String formatDate(String[] yearMonthDay) {
        return OptionManager.DATE_FORMAT.getValue()
		        .replace("y", yearMonthDay[0].substring(2, 4))
		        .replace("m", yearMonthDay[1])
                .replace("d", yearMonthDay[2]);
    }

    /**
     * Formats the passed string array into a time.
     *
     * @param hourMinuteSecond the array that contains the hour, minute and second. The contents of the array must be ordered as follows: <br>
     *                         <code>hourMinuteSecond[0]</code> must be the hour, <br>
     *                         <code>hourMinuteSecond[1]</code> must be the minute, <br>
     *                         <code>hourMinuteSecond[2]</code> must be the second, as the name of the array implies. <br>
     * @return the time, formatted according to the format in the configuration file.
     */
    public static String formatTime(String[] hourMinuteSecond) {
        int hour = Integer.valueOf(hourMinuteSecond[0]) + 7;

        return OptionManager.TIME_FORMAT.getValue()
                .replace("h", String.valueOf(!Boolean.valueOf(OptionManager.TWELVE_HOUR_TIME.getValue()) ? hour : Math.signum(hour - 12) == -1 || hour - 12 == 0 ? hour : hour - 12))
                .replace("m", hourMinuteSecond[1])
                .replace("s", hourMinuteSecond[2]);
    }
}
