package com.doktuhparadox.cryptjournal;

import java.io.IOException;
import java.nio.file.Files;

import javafx.scene.control.ListCell;

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
                String[] yearMonthDay = zuluDate.substring(0, 10).split("-"), hourMinuteSecond = zuluDate.substring(11, 19).split(":");

                this.setText(String.format("%s\n%s at %s", item.getName(), DateAndTime.formatDate(yearMonthDay), DateAndTime.formatTime(hourMinuteSecond)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
