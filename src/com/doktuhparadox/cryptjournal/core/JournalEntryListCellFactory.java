package com.doktuhparadox.cryptjournal.core;

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

        if (!empty) {
	        String[] creationTime = item.fetchProperty("CREATION").split("\\|");
	        StringBuilder builder = new StringBuilder();

	        for (String s : item.getName().split("(?<=\\G......................)")) {
		        if (s.length() == 22) builder.append(s).append("-\n");
		        else builder.append(s);
	        }

	        if (builder.toString().endsWith("-"))
		        builder.replace(builder.lastIndexOf("-"), builder.lastIndexOf("-") + 1, "");

	        this.setText(String.format("%s\n%s at %s", builder.toString(), creationTime[0], creationTime[1]));
        } else {
            this.setText(null);
        }
    }
}
