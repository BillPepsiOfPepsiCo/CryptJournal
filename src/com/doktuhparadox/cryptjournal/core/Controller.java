package com.doktuhparadox.cryptjournal.core;

import com.doktuhparadox.cryptjournal.core.JournalEntry;
import com.doktuhparadox.cryptjournal.core.JournalEntryListCellFactory;
import com.doktuhparadox.cryptjournal.core.option.OptionManager;
import com.doktuhparadox.cryptjournal.util.NodeState;
import com.doktuhparadox.easel.control.keyboard.KeySequence;
import com.doktuhparadox.easel.utils.FXMLWindow;

import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.DialogStyle;
import org.controlsfx.dialog.Dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.web.HTMLEditor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

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
    private Label journalEntryDateLabel;
    @FXML
    private Label journalEntryNameLabel;
    @FXML
    private ListView<JournalEntry> journalEntryListView;
    @FXML
    private HTMLEditor journalContentEditor;

    public static final File journalDir = new File("Journals/");

    @FXML
    protected void initialize() {
        OptionManager.initialize();
        journalEntryListView.setCellFactory(listView -> new JournalEntryListCellFactory());
        if (!journalDir.exists() && journalDir.mkdir()) System.out.println("Created journal entry directory");
        this.attachListeners();
        this.refreshListView();
        //Prevent exceptions
        if (journalEntryListView.getItems().size() == 0) {
            openButton.setDisable(true);
            deleteEntryButton.setDisable(true);
        }
    }

    private void attachListeners() {
        journalEntryListView.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) this.onOpenButtonPressed();
            keyEvent.consume();
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

        createEntryButton.setOnAction(event -> this.onCreateButtonPressed());
        saveButton.setOnAction(event -> this.onSaveButtonPressed());
        deleteEntryButton.setOnAction(event -> this.onDeleteButtonPressed());
        openButton.setOnAction(event -> this.onOpenButtonPressed());
        optionsButton.setOnAction(event -> this.onOptionsButtonPressed());

        //Easter eggs
        KeyCode[] delimiters = {KeyCode.SPACE, KeyCode.BACK_SPACE}; 
        new KeySequence(journalContentEditor, () -> {
            try {
                URL url = this.getClass().getResource("/resources/sound/smoke_weed_erryday.wav");
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }, "WEED", delimiters).attach();

        new KeySequence(journalContentEditor, () -> new FXMLWindow(getClass().getResource("Doge.fxml"), "Doge", 510, 385, false).spawn(), "DOGE", delimiters).attach();
    }

    //**********Button event methods**********\\
    private void onCreateButtonPressed() {
        Optional input = this.createDialog("Create new entry", "Enter entry name").showTextInput();

        if (!input.equals(Optional.empty())) {
            new JournalEntry(input.toString().replace("Optional[", "").replace("]", ""));
            this.refreshListView();
            NodeState.enable(saveButton);
            NodeState.enable(journalContentEditor);
            NodeState.enable(deleteEntryButton);
            NodeState.disable(createEntryButton);
            NodeState.disable(openButton);
            journalContentEditor.requestFocus();
            journalEntryNameLabel.setText(this.getSelectedEntry().getName());
        }
    }

    private void onOpenButtonPressed() {
        String decodedContent = this.getSelectedEntry().read(this.promptForPassword());

        if (decodedContent.equals("BAD_PASSWORD")) {
            this.createDialog("Error", "Incorrect password.").showError();
            return;
        }

        journalContentEditor.setHtmlText(decodedContent);
        NodeState.enable(saveButton);
        NodeState.enable(journalContentEditor);
        NodeState.enable(deleteEntryButton);
        NodeState.disable(createEntryButton);
        journalContentEditor.requestFocus();
        journalEntryNameLabel.setText(this.getSelectedEntry().getName());
    }

    private void onSaveButtonPressed() {
        String password = this.promptForPassword();
        if (password == null) return;

        this.getSelectedEntry().write(journalContentEditor.getHtmlText(), password);
        NodeState.enable(createEntryButton);
        NodeState.enable(openButton);
        NodeState.disable(journalContentEditor);
        NodeState.disable(saveButton);
        journalContentEditor.setHtmlText("");

    }

    private void onDeleteButtonPressed() {
        if (this.createDialog("Delete entry?", "Are you sure you want to delete this entry?").showConfirm() == Dialog.Actions.YES) {
            this.getSelectedEntry().delete();
            this.refreshListView();
            journalEntryNameLabel.setText("");

            if (!journalContentEditor.isDisabled()) {
                NodeState.enable(createEntryButton);
                NodeState.disable(saveButton);
                NodeState.disable(journalContentEditor);
                journalContentEditor.setHtmlText("");
            }

            if (journalEntryListView.getItems().size() == 0) NodeState.disable(deleteEntryButton);
        }
    }

    private void onOptionsButtonPressed() {
        new FXMLWindow(getClass().getResource("OptionWindow.fxml"), "Options", 346, 372, false).spawn();
    }
    //**********Section end, dog**********\\


    private void refreshListView() {
        int currentIndex = journalEntryListView.getSelectionModel().getSelectedIndex() + 1;
        ObservableList<JournalEntry> entries = FXCollections.observableArrayList();

        //noinspection ConstantConditions
        for (File file : journalDir.listFiles())
            if (file != null && file.getName().endsWith(".journal"))
                entries.add(new JournalEntry(file.getName().replace(".journal", "")));

        //Currently a bug with a "ghost entry" that cannot be selected. Believed to be a bug with custom list cell factories.
        journalEntryListView.setItems(entries);
        //There's some stupid ass off-by-one error somewhere in here and I can't find it. It may be an error with custom list cell factories.
        //I honestly don't fucking care if it's on my side because this fixes it and I don't care.
        journalEntryListView.getSelectionModel().select(currentIndex);
    }

    private String promptForPassword() {
        String password;

        while ((password = this.createDialog("Enter password", "Input password for this entry\n(16 chars max)").showTextInput().toString().replace("Optional[", "").replace("]", ""))
                .length() > 16 || password.length() < 16 || password.length() == 0) {

            if (password.equals("Optional.empty")) {
                return null;
            } else if (password.length() > 16) {
                this.createDialog("Error", "Password is too long.").showError();
            } else if (password.length() < 16) {
                //I'm lucky Optional.empty is only 14 characters. Optional.leerundsehrlange
                while (password.length() < 16) password += "=";
                break;
            }
        }

        return password;
    }

    private Dialogs createDialog(String title, String message) {
        return Dialogs.create().masthead(null).lightweight().style(DialogStyle.NATIVE).title(title).message(message);
    }

    private JournalEntry getSelectedEntry() {
        return journalEntryListView.getSelectionModel().getSelectedItem();
    }
}
