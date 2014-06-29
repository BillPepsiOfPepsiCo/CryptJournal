package com.doktuhparadox.cryptjournal;

/**
 * Created and written with IntelliJ IDEA.
 * Module of: CryptJournal
 * User: brennanforrest
 * Date of creation: 6/28/14, at 9:18 PM.
 */
public class DateAndTime {
    public static String formatDate(String[] yearMonthDay) {
        return OptionManager.DATE_FORMAT.getValue()
                .replace("y", yearMonthDay[0])
                .replace("m", yearMonthDay[1])
                .replace("d", yearMonthDay[2]);
    }

    public static String formatTime(String[] hourMinuteSecond) {
        int hour = Integer.valueOf(hourMinuteSecond[0]) + 7;

        return OptionManager.TIME_FORMAT.getValue()
                .replace("h", String.valueOf(Boolean.valueOf(OptionManager.TWENTY_FOUR_HOUR_TIME.getValue()) ? hour : Math.signum(hour - 12) == -1 || hour - 12 == 0 ? hour : hour - 12))
                .replace("m", hourMinuteSecond[1])
                .replace("s", hourMinuteSecond[2]);
    }
}
