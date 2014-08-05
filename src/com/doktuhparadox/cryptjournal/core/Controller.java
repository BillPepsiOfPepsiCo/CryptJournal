package com.doktuhparadox.cryptjournal.core;

import com.doktuhparadox.cryptjournal.option.OptionManager;
import com.doktuhparadox.cryptjournal.util.MethodProxy;
import com.doktuhparadox.cryptjournal.util.NodeState;
import com.doktuhparadox.easel.control.keyboard.KeySequence;
import com.doktuhparadox.easel.io.FileProprietor;
import com.doktuhparadox.easel.utils.FXMLWindow;
import com.doktuhparadox.easel.utils.StringUtils;

import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import resources.Index;

public class Controller {

	@FXML
	private Button aboutButton;
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
	private Button renameButton;
	@FXML
    private Label journalEntryNameLabel;
    @FXML
    private ListView<JournalEntry> journalEntryListView;
    @FXML
    private HTMLEditor journalContentEditor;

    @FXML
    void initialize() {
        if (FileProprietor.pollDir(JournalEntry.journalDir))
            System.out.println("Created journal entry directory at " + JournalEntry.infoDir.getAbsolutePath());
        if (FileProprietor.pollDir(JournalEntry.infoDir))
            System.out.println("Created journal entry metadata directory at " + JournalEntry.infoDir.getAbsolutePath());

	    journalEntryListView.setCellFactory(listView -> new JournalEntryListCellFactory());
		this.attachListeners();
        this.refreshListView();
        //Prevent exceptions
        if (journalEntryListView.getItems().size() == 0) {
            openButton.setDisable(true);
            deleteEntryButton.setDisable(true);
	        renameButton.setDisable(true);
        }
    }

	private ScheduledExecutorService autosaveService;

    private void attachListeners() {
	    journalContentEditor.disabledProperty().addListener((observable, oldValue, newValue) -> {
		    if (!newValue) {
			    int delay = OptionManager.autosaveInterval.value().asInt();
			    System.out.println("Starting autosave service...");
			    autosaveService = Executors.newSingleThreadScheduledExecutor(r -> {
				    Thread t = new Thread(r, "AutosaveServiceDaemon");
				    t.setDaemon(true);
				    return t;
			    });

			    autosaveService.scheduleAtFixedRate(() -> {
				    System.out.println("Autosaving...");
				    saveEntry(true);
			    }, delay, delay, TimeUnit.SECONDS);
		    } else {
			    System.out.println("Canceling autosave service...");
			    autosaveService.shutdown();
		    }
	    });

	    journalEntryListView.setOnKeyPressed(keyEvent -> {
		    if (keyEvent.getCode() == KeyCode.R) this.refreshListView();
	    });

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
	            NodeState.disable(renameButton);
            } else {
                NodeState.enable(openButton);
                NodeState.enable(deleteEntryButton);
	            NodeState.enable(renameButton);
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
	    renameButton.setOnAction(event -> this.renameEntry());

        aboutButton.setOnAction(event -> {
            Parent root = null;
	        Stage stage = new Stage(StageStyle.UNIFIED);

            try {
                root = FXMLLoader.load(this.getClass().getResource("/com/doktuhparadox/cryptjournal/etc/AboutMenu.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
	            return;
            }

            Scene scene = new Scene(root, 336, 312);

	        if (OptionManager.theme.getValue().equals("dark"))
		        root.getStylesheets().add(Index.darkThemeStylesheet.toExternalForm());

            stage.initStyle(StageStyle.UNIFIED);
            stage.setTitle("About CryptJournal");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        });

	    optionsButton.setOnAction(event -> {
		    Parent root = null;
		    Stage stage = new Stage();
		    FXMLLoader loader = null;

		    try {
			    loader = new FXMLLoader(getClass().getResource("/com/doktuhparadox/cryptjournal/option/OptionWindow.fxml"));
			    root = loader.load();
		    } catch (IOException e) {
			    e.printStackTrace();
		    }

		    Scene scene = new Scene(root, 346, 372);

		    stage.initStyle(StageStyle.UNIFIED);
		    stage.setTitle("Options");
		    stage.setScene(scene);
		    stage.setResizable(false);
		    stage.show();
	    });

        //Easter eggs
	    new KeySequence(journalContentEditor, () -> new FXMLWindow(getClass().getResource("Doge.fxml"), "Doge", 510, 385, false).show(), "DOGE", KeyCode.SPACE, KeyCode.BACK_SPACE).attach();
    }

	//**********Event methods**********\\
    void createNewEntry() {
        Optional input = this.createDialog("Create new entry", "Enter entry name").showTextInput();

        if (!input.equals(Optional.empty())) {
	        JournalEntry newEntry = new JournalEntry(String.valueOf(input.get()));

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
		        this.createDialog("Exception Raised", "Exception caught when trying to create new journal entry (read the first line and you may understand the issue):").showException(e);
		        return;
	        }

	        this.refreshListView();
	        NodeState.enable(saveButton);
	        NodeState.enable(journalContentEditor);
	        NodeState.disable(deleteEntryButton);
	        NodeState.disable(createEntryButton);
	        NodeState.disable(openButton);
	        NodeState.disable(journalEntryListView);
	        NodeState.disable(renameButton);
	        journalEntryListView.getSelectionModel().select(newEntry);
	        journalEntryNameLabel.setText(newEntry.getName());
	        MethodProxy.setDockBadge("*");
        }
    }

    void openEntry() {
        JournalEntry currentEntry = this.getSelectedEntry();
        String decodedContent;

        try {
            decodedContent = currentEntry.fetchProperty("LAST_SAVE_WAS_AUTOSAVE").equals("true") ? currentEntry.read("$") : currentEntry.read(this.promptForPassword());

            if (decodedContent.equals("BAD_PASSWORD")) {
                this.createDialog("Error", "Incorrect password.").showError();
                return;
            }
        } catch (NoSuchElementException e) {
            return;
        }

        journalEntryNameLabel.setText(currentEntry.getName());
        journalContentEditor.setHtmlText(decodedContent);
	    NodeState.enable(saveButton);
        NodeState.enable(journalContentEditor);
        NodeState.disable(journalEntryListView);
        NodeState.disable(openButton);
        NodeState.disable(createEntryButton);
        NodeState.disable(deleteEntryButton);
		NodeState.disable(renameButton);
        journalContentEditor.requestFocus();

        MethodProxy.setDockBadge("*");
    }

    void saveEntry(boolean isAutosave) {
        if (isAutosave) {
	        String text = journalContentEditor.getHtmlText();
	        if (StringUtils.emptyOrNull(text)) return;
            this.getSelectedEntry().write(text, "$");
        } else {
            String password;

            try {
                password = this.promptForPassword();
            } catch (NoSuchElementException e) {
                return;
            }

			this.getSelectedEntry().write(journalContentEditor.getHtmlText(), password);

			if (journalEntryListView.getItems().size() > 0) {
				NodeState.enable(openButton);
				NodeState.enable(deleteEntryButton);
			}

			NodeState.enable(createEntryButton);
			NodeState.enable(journalEntryListView);
			NodeState.enable(renameButton);
			NodeState.disable(journalContentEditor);
			NodeState.disable(saveButton);
			journalEntryListView.requestFocus();
			journalEntryNameLabel.setText("");
			journalContentEditor.setHtmlText("");
		}

		MethodProxy.setDockBadge(null);
	}

    void renameEntry() {
        Optional<String> newName = Dialogs.create().masthead(null).message("Enter new entry name").showTextInput();
        try {
            String newNameStr = newName.get();
            if (StringUtils.emptyOrNull(newNameStr)) return;
            this.getSelectedEntry().rename(newNameStr);
        } catch (NoSuchElementException ignored) {
		}

        this.refreshListView();
    }

    void deleteEntry() {
        if (this.createDialog("Delete entry?", "Are you sure you want to delete this entry?").showConfirm() == Dialog.Actions.YES) {
            this.getSelectedEntry().delete();
            this.refreshListView();

	        journalEntryListView.getSelectionModel().select(-1);

	        if (journalEntryListView.getItems().size() == 0) {
		        NodeState.disable(openButton);
                NodeState.disable(deleteEntryButton);
	            NodeState.disable(renameButton);
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
    //**********Section end, dog**********\\


    private void refreshListView() {
        journalEntryListView.getItems().clear();

	    if (JournalEntry.journalDir.exists()) {
		    System.out.println("Refreshing list view");

	        FileProprietor.ls(JournalEntry.journalDir).stream()
			        .filter(f -> f.getName().endsWith(".journal") && Files.exists(Paths.get("Journals/.metadata/" + f.getName() + "metadata")))
			        .map(f -> new JournalEntry(f.getName()))
			        .forEach(e -> journalEntryListView.getItems().add((JournalEntry) e));
	    }
    }

    private String promptForPassword() throws NoSuchElementException {
        return this.createDialog("Enter password", "Password for this entry:").showTextInput().get();
    }

    private Dialogs createDialog(String title, String message) {
	    return Dialogs.create().masthead(null).title(title).message(message);
    }

    private JournalEntry getSelectedEntry() {
        return journalEntryListView.getSelectionModel().getSelectedItem();
    }
}
