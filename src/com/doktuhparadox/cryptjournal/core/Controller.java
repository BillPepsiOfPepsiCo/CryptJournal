package com.doktuhparadox.cryptjournal.core;

import com.doktuhparadox.cryptjournal.option.OptionManager;
import com.doktuhparadox.cryptjournal.util.MethodProxy;
import com.doktuhparadox.cryptjournal.util.NodeState;
import com.doktuhparadox.easel.control.keyboard.KeySequence;
import com.doktuhparadox.easel.io.FileProprietor;
import com.doktuhparadox.easel.utils.Clippy;
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
import java.util.Timer;
import java.util.TimerTask;

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

    private final String defaultPassword = "dongerlord";
	private Timer autosaveService;

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

	    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
		    if (!this.journalContentEditor.isDisabled()) {
			    System.out.println("Application termination requested while entry is being edited, performing autosave...");
			    this.saveEntry(true);
		    }
	    }));
    }


    private void attachListeners() {
	    journalContentEditor.disabledProperty().addListener((observable, oldValue, newValue) -> {
		    if (!newValue) {
			    int delay = OptionManager.autosaveInterval.value().asInt() * 1000;
			    System.out.println("Starting autosave service...");
			    autosaveService = new Timer("AutosaveDaemon", true);
			    autosaveService.scheduleAtFixedRate(new TimerTask() {
				    @Override
				    public void run() {
					    System.out.println("Autosaving...");
					    saveEntry(true);
				    }
			    }, delay, delay);
		    } else {
			    System.out.println("Canceling autosave service...");
			    autosaveService.cancel();
		    }
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

		    try {
			    root = FXMLLoader.load(this.getClass().getResource("/com/doktuhparadox/cryptjournal/option/OptionWindow.fxml"));
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
        KeyCode[] delimiters = {KeyCode.SPACE, KeyCode.BACK_SPACE};
        new KeySequence(journalContentEditor, () -> Clippy.playSound("/resources/sound/smoke_weed_erryday.wav"), "WEED", delimiters).attach();
	    new KeySequence(journalContentEditor, () -> new FXMLWindow(getClass().getResource("Doge.fxml"), "Doge", 510, 385, false).show(), "DOGE", delimiters).attach();
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
        String decodedContent;

        //Tests to see if the password for this entry is empty (an empty password is sixteen equal signs) and skips the password prompt if so
        if ((decodedContent = this.getSelectedEntry().read(this.defaultPassword)).equals("BAD_PASSWORD")) {
            try {
                decodedContent = this.getSelectedEntry().read(this.promptForPassword());
            } catch (NoSuchElementException e) {
                return; //The user either pressed "cancel" or entered nothing
            }

			if (decodedContent.equals("BAD_PASSWORD")) {
				this.createDialog("Error", "Incorrect password.").showError();
				return;
			}
		}

        journalContentEditor.setHtmlText(decodedContent);
	    journalContentEditor.requestFocus();
	    NodeState.enable(saveButton);
        NodeState.enable(journalContentEditor);
        NodeState.disable(journalEntryListView);
        NodeState.disable(openButton);
        NodeState.disable(createEntryButton);
        NodeState.disable(deleteEntryButton);
		NodeState.disable(renameButton);
		journalEntryNameLabel.setText(this.getSelectedEntry().getName());
		MethodProxy.setDockBadge("*");
	}

    void saveEntry(boolean isAutosave) {
        if (isAutosave) {
	        String text = journalContentEditor.getHtmlText();
	        if (StringUtils.emptyOrNull(text)) return;
	        this.getSelectedEntry().write(text, this.defaultPassword);
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
            this.getSelectedEntry().rename(newName.get());
		} catch (NoSuchElementException ignored) {
		}
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

        if (JournalEntry.journalDir.exists()) //noinspection ConstantConditions
	        FileProprietor.ls(JournalEntry.journalDir).stream()
			        .filter(f -> f.getName().endsWith(".journal") && Files.exists(Paths.get("Journals/.metadata/" + f.getName() + "metadata")))
                    .map(f -> new JournalEntry(f.getName()))
                    .forEach(e -> journalEntryListView.getItems().add((JournalEntry) e));
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
