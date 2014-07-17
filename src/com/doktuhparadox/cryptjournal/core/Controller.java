package com.doktuhparadox.cryptjournal.core;

import com.doktuhparadox.cryptjournal.util.MethodProxy;
import com.doktuhparadox.cryptjournal.util.NodeState;
import com.doktuhparadox.easel.control.keyboard.KeySequence;
import com.doktuhparadox.easel.io.FileProprietor;
import com.doktuhparadox.easel.utils.Clippy;
import com.doktuhparadox.easel.utils.FXMLWindow;

import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.web.HTMLEditor;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class Controller {

    @FXML
    private Button createEntryButton;
    @FXML
    private Button deleteEntryButton;
    @FXML
    private Button openButton;
    @FXML
    private Button optionsButton;
    @FXML
    private Button saveButton;
    @FXML
    private Label journalEntryNameLabel;
    @FXML
    private ListView<JournalEntry> journalEntryListView;
    @FXML
    private HTMLEditor journalContentEditor;

	@FXML
	void initialize() {
	    journalEntryListView.setCellFactory(listView -> new JournalEntryListCellFactory());
		if (FileProprietor.pollDir(JournalEntry.journalDir))
			System.out.println("Created journal entry directory at " + JournalEntry.infoDir.getAbsolutePath());
		if (FileProprietor.pollDir(JournalEntry.infoDir))
			System.out.println("Created journal entry metadata directory at " + JournalEntry.infoDir.getAbsolutePath());
		this.attachListeners();
        this.refreshListView();
        //Prevent exceptions
        if (journalEntryListView.getItems().size() == 0) {
            openButton.setDisable(true);
            deleteEntryButton.setDisable(true);
        }

	    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
		    if (!this.journalContentEditor.isDisabled()) {
			    System.out.println("Application termination requested while entry is being edited, performing autosave...");
			    this.saveEntry(true);
		    }
	    }));
    }

    private void attachListeners() {
        journalEntryListView.setOnKeyPressed(keyEvent -> {
            if (this.getSelectedEntry() != null) {
	            if (keyEvent.getCode().equals(KeyCode.ENTER)) this.openEntry();
	            if (keyEvent.getCode().equals(KeyCode.DELETE)) this.deleteEntry();
            }
        });

        journalEntryListView.itemsProperty().addListener(observable -> {
            if (journalEntryListView.getItems().size() == 0) {
                NodeState.disable(openButton);
                NodeState.disable(deleteEntryButton);
            } else {
                NodeState.enable(openButton);
                NodeState.enable(deleteEntryButton);
            }
        });

	    journalEntryListView.getFocusModel().focusedItemProperty().addListener((observable, oldValue, newValue) -> {
		    if (newValue == null) {
			    NodeState.disable(openButton);
			    NodeState.disable(deleteEntryButton);
		    } else {
			    NodeState.enable(openButton);
			    NodeState.enable(deleteEntryButton);
		    }
	    });

	    createEntryButton.setOnAction(event -> this.createNewEntry());
	    saveButton.setOnAction(event -> this.saveEntry(false));
	    deleteEntryButton.setOnAction(event -> this.deleteEntry());
	    openButton.setOnAction(event -> this.openEntry());
	    optionsButton.setOnAction(event -> this.openOptionsWindow());

        //Easter eggs
        KeyCode[] delimiters = {KeyCode.SPACE, KeyCode.BACK_SPACE};
        new KeySequence(journalContentEditor, () -> Clippy.playSound("/resources/sound/smoke_weed_erryday.wav"), "WEED", delimiters).attach();
	    new KeySequence(journalContentEditor, () -> new FXMLWindow(getClass().getResource("Doge.fxml"), "Doge", 510, 385, false).show(), "DOGE", delimiters).attach();
    }

	//**********Event methods**********\\
	public void createNewEntry() {
        Optional input = this.createDialog("Create new entry", "Enter entry name").showTextInput();

        if (!input.equals(Optional.empty())) {
            JournalEntry newEntry = new JournalEntry(input.toString().replace("Optional[", "").replace("]", ""));
	        try {
		        if (newEntry.create()) {
			        System.out.println("Created new journal entry " + newEntry.getName());
		        } else {
			        if (newEntry.getFile().exists()) {
				        this.createDialog("Could not create new entry", "An entry with that name already exists.").showError();
			        } else {
				        this.createDialog("Could not create new entry", "Unknown error.").showError();
			        }
			        return;
		        }
	        } catch (IOException e) {
		        Dialogs.create().masthead(null).title("Exception").message("Exception caught when trying to create new journal entry").showException(e);
	        }

	        this.refreshListView();
	        NodeState.enable(saveButton);
	        NodeState.enable(journalContentEditor);
	        NodeState.disable(deleteEntryButton);
	        NodeState.disable(createEntryButton);
	        NodeState.disable(openButton);
	        NodeState.disable(journalEntryListView);
	        journalEntryListView.getSelectionModel().select(newEntry);
	        journalEntryNameLabel.setText(newEntry.getName());
	        MethodProxy.setDockBadge("*");
        }
    }

	public void openEntry() {
		String decodedContent;

        //Tests to see if the password for this entry is empty (an empty password is sixteen equal signs) and skips the password prompt if so
		if ((decodedContent = this.getSelectedEntry().read(this.defaultPassword())).equals("BAD_PASSWORD")) {
			decodedContent = this.getSelectedEntry().read(this.promptForPassword());
			if (decodedContent.equals("BAD_PASSWORD")) {
				this.createDialog("Error", "Incorrect password.").showError();
				return;
			}
		}

        journalContentEditor.setHtmlText(decodedContent);
        NodeState.enable(saveButton);
        NodeState.enable(journalContentEditor);
        NodeState.disable(journalEntryListView);
        NodeState.disable(openButton);
        NodeState.disable(createEntryButton);
        NodeState.disable(deleteEntryButton);
        journalEntryNameLabel.setText(this.getSelectedEntry().getName());
		MethodProxy.setDockBadge("*");
	}

	public void saveEntry(boolean isAutosave) {
		if (isAutosave) {
			this.getSelectedEntry().write(journalContentEditor.getHtmlText(), this.defaultPassword());
		} else {
			String password = this.promptForPassword();
			if (password == null) return;

			this.getSelectedEntry().write(journalContentEditor.getHtmlText(), password);

			if (journalEntryListView.getItems().size() > 0) {
				NodeState.enable(openButton);
				NodeState.enable(deleteEntryButton);
			}

			NodeState.enable(createEntryButton);
			NodeState.enable(journalEntryListView);
			NodeState.disable(journalContentEditor);
			NodeState.disable(saveButton);
			journalEntryListView.requestFocus();
			journalEntryNameLabel.setText("");
			journalContentEditor.setHtmlText("");
		}

		MethodProxy.setDockBadge(null);
	}

	public void deleteEntry() {
		if (this.createDialog("Delete entry?", "Are you sure you want to delete this entry?").showConfirm() == Dialog.Actions.YES) {
            this.getSelectedEntry().delete();
            this.refreshListView();
            journalEntryListView.getSelectionModel().select(-1);

            if (journalEntryListView.getItems().size() == 0) {
                NodeState.disable(openButton);
                NodeState.disable(deleteEntryButton);
            }

            journalEntryNameLabel.setText("");

            if (!journalContentEditor.isDisabled()) {
                NodeState.enable(createEntryButton);
                NodeState.enable(openButton);
                NodeState.disable(saveButton);
                NodeState.disable(journalContentEditor);
                journalContentEditor.setHtmlText("");
            }
        }
    }

	private void openOptionsWindow() {
		FXMLWindow optionsWindow = new FXMLWindow(getClass().getResource("option/OptionWindow.fxml"), "Options", 346, 372, false);
		optionsWindow.stage.setAlwaysOnTop(true);
		optionsWindow.show(StageStyle.UNIFIED, null);
	}
    //**********Section end, dog**********\\


    private void refreshListView() {
        ObservableList<JournalEntry> entries = FXCollections.observableArrayList();

        //noinspection ConstantConditions
	    for (File file : JournalEntry.journalDir.listFiles()) {
		    if (file.getName().endsWith(".journal")) {
			    JournalEntry newEntry = new JournalEntry(file.getName().replace(".journal", ""));
			    if (newEntry.getFile().exists() && newEntry.getMetadataFile().exists()) {
				    entries.add(newEntry);
			    } else {
				    System.out.printf("Metadata for entry %s was not found and it will not be indexed.\n", newEntry.getName());
			    }
		    }
	    }

        journalEntryListView.setItems(entries);
    }

    private String promptForPassword() {
        String password;

	    int keyLength = EncryptionAlgorithm.valueOf(this.getSelectedEntry().fetchProperty("ENCRYPTION").split("/")[0]).keyLength;
	    while ((password = this.createDialog("Enter password", String.format("Input password for this entry\n(%s chars max)", keyLength)).showTextInput().toString().replace("Optional[", "").replace("]", ""))
			    .length() > keyLength || password.length() < keyLength) {

            if (password.equals("Optional.empty")) {
                return null;
            } else if (password.length() > keyLength) {
                this.createDialog("Error", "Password is too long.").showError();
            } else if (password.length() < keyLength) {
	            while (password.length() < keyLength) password += "=";
	            break;
            }
	    }

        return password;
    }

    private Dialogs createDialog(String title, String message) {
	    return Dialogs.create().masthead(null).title(title).message(message);
    }

	private String defaultPassword() {
		String s = "";

		while (s.length() < EncryptionAlgorithm.valueOf(this.getSelectedEntry().fetchProperty("ENCRYPTION").split("/")[0]).keyLength)
			s += "=";

		return s;
	}

    private JournalEntry getSelectedEntry() {
        return journalEntryListView.getSelectionModel().getSelectedItem();
    }
}
