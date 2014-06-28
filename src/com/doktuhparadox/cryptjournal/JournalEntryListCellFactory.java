package com.doktuhparadox.cryptjournal;

import javafx.scene.control.ListCell;

import java.nio.file.Files;
import java.io.IOException;

/**
 * Created and written with IntelliJ IDEA.
 * Module of: CryptJournal
 * User: brennanforrest
 * Date of creation: 6/27/14, at 12:57 PM.
 */
public class JournalEntryListCellFactory extends ListCell<JournalEntry> {
    @Override
    public void updateItem(JournalEntry item, boolean empty) {
        super.updateItem(item, empty);

        if (item != null) {
            try {
                String zuluDate = Files.getAttribute(item.getFile().toPath(), "creationTime").toString();
                String[] dayMonthYear = zuluDate.substring(0, 10).split("-"), hourMinuteSecond = zuluDate.substring(11, 19).split(":");
                int hour = Integer.valueOf(hourMinuteSecond[0]) + 7;

                //TODO: add configuration options for date and time format, as well as 24 hour time display
                String formattedDayMonthYear = String.format("%s/%s/%s", dayMonthYear[1], dayMonthYear[2], dayMonthYear[0].replaceFirst("20", "")),
                formattedHourMinuteSecond = String.format("%s:%s:%s", Math.signum(hour - 12) == -1  || hour - 12 == 0 ? hour : hour - 12, hourMinuteSecond[1], hourMinuteSecond[2]);

                this.setText(String.format("%s\n%s at %s", item.getName(), formattedDayMonthYear, formattedHourMinuteSecond));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
