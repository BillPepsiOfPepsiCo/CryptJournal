package com.doktuhparadox.cryptjournal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.web.HTMLEditor;

import java.io.File;

import java.util.Optional;

import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.DialogStyle;
import org.controlsfx.dialog.Dialogs;

public class Controller {

    @FXML public Button createEntryButton;
    @FXML public Button deleteEntryButton;
    @FXML public Button openButton;
    @FXML public Button saveButton;
    @FXML public Label journalEntryDateLabel;
    @FXML public Label journalEntryNameLabel;
    @FXML public ListView<JournalEntry> journalEntryListView;
    @FXML public HTMLEditor journalContentEditor;

    public static final File journalDir = new File("Journals/");

    @FXML
    protected void initialize() {
        journalEntryListView.setCellFactory(listView -> new JournalEntryListCellFactory());
        if (!journalDir.exists() && !journalDir.mkdir()) System.out.println("Created journal entry directory");
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
                openButton.setDisable(true);
                deleteEntryButton.setDisable(true);
            } else {
                openButton.setDisable(false);
                deleteEntryButton.setDisable(false);
            }
        });

        createEntryButton.setOnAction(event -> this.onCreateButtonPressed());
        saveButton.setOnAction(event -> this.onSaveButtonPressed());
        deleteEntryButton.setOnAction(event -> this.onDeleteButtonPressed());
        openButton.setOnAction(event -> this.onOpenButtonPressed());
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
        if (decodedContent.equals("BAD_PASSWORD")) this.createDialog("Error", "Incorrect password.").showError();

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
        NodeState.disable(journalContentEditor);
        NodeState.disable(saveButton);
        journalContentEditor.setHtmlText("");

    }

    private void onDeleteButtonPressed() {
        if (this.createDialog("Delete entry?", "Are you sure you want to delete this entry?").showConfirm() == Dialog.Actions.YES) {
            this.getSelectedEntry().attemptDelete();
            this.refreshListView();
            if (journalEntryListView.getItems().size() == 0) NodeState.disable(deleteEntryButton);
        }
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
        Optional passObj = this.createDialog("Enter password", "Input password for this entry (16 chars max)").showTextInput();
        if (passObj != Optional.empty()) {
            String password = passObj.toString().replace("Optional[", "").replace("]", "");
            while (password.length() < 16)
                password += "=";

            return password;
        }

        return null;
    }

    private Dialogs createDialog(String title, String message) {
        return Dialogs.create().masthead(null).style(DialogStyle.NATIVE).title(title).message(message);
    }

    private JournalEntry getSelectedEntry() {
        return journalEntryListView.getSelectionModel().getSelectedItem();
    }
}
