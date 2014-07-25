package com.doktuhparadox.cryptjournal.option;

import com.doktuhparadox.cryptjournal.core.EncryptionAlgorithm;
import com.doktuhparadox.easel.options.Option;
import com.doktuhparadox.easel.options.SimpleOptionsHandler;
import com.doktuhparadox.easel.utils.RuntimeUtils;

import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

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
	private ComboBox<String> encryptionAlgorithmComboBox;
	@FXML
	private CheckBox useDarkThemeCheckbox;
	@FXML
	private TextField autosaveIntervalTextField;
	@FXML
	private CheckBox cachePasswordsCheckBox;
	@FXML
	private TextField dateFormatTextField;
	@FXML
	private TextField timeFormatTextField;
	@FXML
	private Button applyButton;

	private static final File configFile = new File("Options.txt");
	public static final SimpleOptionsHandler optionHandler = new SimpleOptionsHandler(configFile);

	public static final Option<String> theme = new Option<>(optionHandler, "theme", "light"),
			dateFormat = new Option<>(optionHandler, "date format", "dd/mm/yyyy"),
			timeFormat = new Option<>(optionHandler, "time format", "hh:mm:ss"),
			encryptionAlgorithm = new Option<>(optionHandler, "encryption", "AES");
	public static final Option<Boolean> cachePasswords = new Option<>(optionHandler, "cache passwords", false);
	public static final Option<Integer> autosaveInterval = new Option<>(optionHandler, "autosave interval", 60);

	private boolean promptForRestartOnApply = false;

	@FXML
	public void initialize() {
		root.getStylesheets().add(Index.rootTweaksStylesheet.toExternalForm());

		if (theme.getValue().equals("dark")) {
			root.getStylesheets().add(Index.darkThemeStylesheet.toExternalForm());
			useDarkThemeCheckbox.setSelected(true);
		}

		dateFormatTextField.setFont(Font.loadFont(this.getClass().getResourceAsStream("/resources/font/mplus-1m-regular.ttf"), 12));
		timeFormatTextField.setFont(Font.loadFont(this.getClass().getResourceAsStream("/resources/font/mplus-1m-regular.ttf"), 12));
		autosaveIntervalTextField.setFont(Font.loadFont(this.getClass().getResourceAsStream("/resources/font/mplus-1m-regular.ttf"), 12));

		dateFormatTextField.setText(dateFormat.getValue());
		timeFormatTextField.setText(timeFormat.getValue());
		autosaveIntervalTextField.setText(String.valueOf(autosaveInterval.getValue()));

		encryptionAlgorithmComboBox.getItems().addAll(Arrays.asList(EncryptionAlgorithm.values()).stream().map(Enum::toString).collect(Collectors.toList()));
		encryptionAlgorithmComboBox.getSelectionModel().select(encryptionAlgorithm.getValue());


		useDarkThemeCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
			theme.set(newValue ? "dark" : "light");
			promptForRestartOnApply = true;
		});

		autosaveIntervalTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			autosaveInterval.set(Integer.valueOf(autosaveIntervalTextField.getText()));
			promptForRestartOnApply = true;
		});

		cachePasswordsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> cachePasswords.set(newValue));
		dateFormatTextField.focusedProperty().addListener((observable, oldValue, newValue) -> dateFormat.set(dateFormatTextField.getText()));
		timeFormatTextField.focusedProperty().addListener((observable, oldValue, newValue) -> timeFormat.set(timeFormatTextField.getText()));
		encryptionAlgorithmComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> encryptionAlgorithm.set(encryptionAlgorithmComboBox.getValue()));
	}

	@FXML
	public void onApplyButtonPressed() {
		if (this.promptForRestartOnApply && Dialogs.create().masthead(null).title("Restart?").message("An option that requires a restart to take effect was changed. Would you like to restart?").showConfirm() == Dialog.Actions.YES) {
			RuntimeUtils.restart("CryptJournal.jar");
			return;
		}

		root.getScene().getWindow().hide();
		promptForRestartOnApply = false;
	}
}
