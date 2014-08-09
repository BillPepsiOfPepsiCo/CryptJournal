package com.doktuhparadox.cryptjournal.core;

import com.doktuhparadox.easel.utils.StringUtils;

import javafx.scene.control.ListCell;

/**
 * Created and written with IntelliJ IDEA.
 * Module of: CryptJournal
 * User: brennanforrest
 * Date of creation: 6/27/14, at 12:57 PM.
 */
class JournalEntryListCellFactory extends ListCell<JournalEntry> {
    @Override
    public void updateItem(JournalEntry item, boolean empty) {
        super.updateItem(item, empty);

        if (!empty) {
            String[] creationTime = item.fetchProperty("CREATION").split(" ");
            StringBuilder builder = new StringBuilder();

            String regex = "(?<=\\G..............................)";

            for (String s : item.getName().split(regex)) {
                builder.append(s);

                if (s.length() == StringUtils.countMatches(regex, '.', false)) {
                    builder.append(!Character.isSpaceChar(s.charAt(s.length() - 1)) ? "-\n" : "\n");
                }
            }

            this.setText(String.format("%s\n%s at %s", builder.toString(), creationTime[0], creationTime[1]));
        } else {
            this.setText(null);
        }
    }
}
