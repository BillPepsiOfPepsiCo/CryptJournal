package com.doktuhparadox.cryptjournal;

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
            this.setText(item.getName());
        }
    }
}
