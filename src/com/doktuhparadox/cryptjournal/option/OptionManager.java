package com.doktuhparadox.cryptjournal.option;

import com.doktuhparadox.cryptjournal.util.MethodProxy;
import com.doktuhparadox.easel.options.Option;
import com.doktuhparadox.easel.options.SimpleOptionsHandler;
import com.doktuhparadox.easel.utils.RuntimeUtils;

import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Window;

import java.io.File;
import java.util.function.Predicate;

import resources.Index;

/**
 * Created and written with IntelliJ IDEA.
 * Module of: CryptJournal
 * User: brennanforrest
 * Date of creation: 6/28/14, at 10:32 PM.
 */
public class OptionManager {

    @FXML
    private AnchorPane root;
    @FXML
    private Button applyButton;
    @FXML
    private CheckBox useStrongEncryptionCheckbox;
    @FXML
    private ComboBox<String> timeFormatComboBox;
    @FXML
    private ComboBox<String> dateFormatComboBox;
    @FXML
    private TextField autosaveIntervalTextField;
    @FXML
    private CheckBox cachePasswordsCheckBox;
    @FXML
    private CheckBox enableLoggingCheckbox;
    @FXML
    private CheckBox useDarkThemeCheckbox;
    @FXML
    private TextField keyObtentionIterationsTextField;
    @FXML
    private Label keyObtentionIterationsLabel;

    private static final File configFile = new File("Options.txt");
    public static final SimpleOptionsHandler optionHandler = new SimpleOptionsHandler(configFile, false);

    public static final Option<String> theme = new Option<>(optionHandler, "theme", "light"),
            dateFormat = new Option<>(optionHandler, "date format", "dd/MM/yyyy"),
            timeFormat = new Option<>(optionHandler, "time format", "hh:mm:ss");

    public static final Option<Boolean> cachePasswords = new Option<>(optionHandler, "cache passwords", false),
            useStrongEncryption = new Option<>(optionHandler, "use strong encryption", true),
            loggingEnabled = new Option<>(optionHandler, "enable logging", false);
    public static final Option<Integer> autosaveInterval = new Option<>(optionHandler, "autosave interval", 60),
            keyObtentionIterations = new Option<>(optionHandler, "key obtention iterations", 1000);

    private boolean promptForRestartOnApply;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            root.getStylesheets().add(Index.rootTweaksStylesheet.toExternalForm());

            if (theme.getValue().equals("dark")) {
                root.getStylesheets().add(Index.darkThemeStylesheet.toExternalForm());
                useDarkThemeCheckbox.setSelected(true);
            }

            enableLoggingCheckbox.setSelected(loggingEnabled.value().asBoolean());
            autosaveIntervalTextField.setFont(Font.loadFont(this.getClass().getResourceAsStream("/resources/font/mplus-1m-regular.ttf"), 14));
            keyObtentionIterationsTextField.setFont(Font.loadFont(this.getClass().getResourceAsStream("/resources/font/mplus-1m-regular.ttf"), 14));

            dateFormatComboBox.getItems().setAll("dd/MM/yyyy", "MM/dd/yyyy", "yyyy/MM/dd", "Custom");
            timeFormatComboBox.getItems().setAll("hh:mm:ss", "HH:mm:ss", "hh:mm", "HH:mm");

            dateFormatComboBox.getSelectionModel().select(dateFormat.getValue());
            timeFormatComboBox.getSelectionModel().select(timeFormat.getValue());
            autosaveIntervalTextField.setText(autosaveInterval.value().asString());
            keyObtentionIterationsTextField.setText(keyObtentionIterations.value().asString());

            dateFormatComboBox.setTooltip(new Tooltip("Sets the date format for the list view."));
            timeFormatComboBox.setTooltip(new Tooltip("Sets the time format for the list view."));
            autosaveIntervalTextField.setTooltip(new Tooltip("Sets the amount of time (in seconds)\nbetween autosaves."));
            cachePasswordsCheckBox.setTooltip(new Tooltip("N.Y.I."));
            keyObtentionIterationsTextField.setTooltip(new Tooltip("Higher number = more secure, but slower."));
            useDarkThemeCheckbox.setTooltip(new Tooltip("Applies the sexy dark theme."));

            Tooltip t = new Tooltip();
            if (!MethodProxy.strongEncryptionAvailable) {
                useStrongEncryptionCheckbox.setSelected(false);
                useStrongEncryptionCheckbox.setDisable(true);
                useStrongEncryption.setValue(false);
                t.setText("This is disabled because you don\'t have the " +
                        "Java Cryptography Extension Unlimited Strength Jurisdiction Policy Files 8 installed.\nInstall them" +
                        " to enable this option. :D");
            } else {
                useStrongEncryptionCheckbox.setSelected(useStrongEncryption.value().asBoolean());
                t.setText("If checked, uses the StrongTextEncryptor from Jasypt. If it\'s not, uses " +
                        "StandardPBEStringEncryptor\nfrom Jasypt (using PBEWithMD5AndDES as the algorithm)");
            }

            useStrongEncryptionCheckbox.setTooltip(t);

            keyObtentionIterationsLabel.setVisible(!useStrongEncryptionCheckbox.isSelected());
            keyObtentionIterationsTextField.setVisible(!useStrongEncryptionCheckbox.isSelected());

            useDarkThemeCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                theme.setValue(newValue ? "dark" : "light");
                promptForRestartOnApply = true;
            });

            autosaveIntervalTextField.focusedProperty().addListener((observable, oldValue, newValue) -> autosaveInterval.setValue(Integer.valueOf(autosaveIntervalTextField.getText())));
            cachePasswordsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> cachePasswords.setValue(newValue));
            dateFormatComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> dateFormat.setValue(dateFormatComboBox.getSelectionModel().getSelectedItem()));
            timeFormatComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> timeFormat.setValue(timeFormatComboBox.getSelectionModel().getSelectedItem()));
            keyObtentionIterationsTextField.focusedProperty().addListener((observable, oldValue, newValue) -> keyObtentionIterations.setValue(Integer.valueOf(keyObtentionIterationsTextField.getText())));
            useStrongEncryptionCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                useStrongEncryption.setValue(newValue);
                keyObtentionIterationsLabel.setVisible(!newValue);
                keyObtentionIterationsTextField.setVisible(!newValue);
            });
            enableLoggingCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> loggingEnabled.setValue(newValue));

            //Kind of boilerplate, but it's aight.
            Predicate<String> numericInputEnsurancePredicate = s -> s.matches("([0-9]+)|(^$)");

            autosaveIntervalTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!numericInputEnsurancePredicate.test(newValue)) autosaveIntervalTextField.setText(oldValue);
            });
            keyObtentionIterationsTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!numericInputEnsurancePredicate.test(newValue)) keyObtentionIterationsTextField.setText(oldValue);
            });
        });
    }

    @FXML
    public void onApplyButtonPressed() {
        if (this.promptForRestartOnApply && Dialogs.create().masthead(null).title("Restart?").message("An option that requires a restart to take effect was changed. Would you like to restart?").showConfirm() == Dialog.Actions.YES) {
            RuntimeUtils.restart("CryptJournal.jar");
            return;
        }

        Window window = root.getScene().getWindow();
        window.hide();
        window.getOnCloseRequest().handle(null); //Calling window.hide does not dispatch a window close event
        promptForRestartOnApply = false;
    }
}
