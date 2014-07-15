package com.doktuhparadox.cryptjournal.core.option;

import com.doktuhparadox.easel.utils.RuntimeUtils;

import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import static com.doktuhparadox.cryptjournal.core.option.OptionsManager.optionHandler;

/**
 * Created and written with IntelliJ IDEA.
 * Module of: CryptJournal
 * User: brennanforrest
 * Date of creation: 6/28/14, at 10:32 PM.
 */
public class OptionsWindowController {

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
	private CheckBox twelveHourTimeCheckbox;
	@FXML
	private Button applyButton;

	private boolean promptForRestartOnApply = false;

	@FXML
	protected void initialize() {
		if (optionHandler.get("theme").equals("dark")) {
			root.getStylesheets().add("/resources/css/DarkTheme.css");
			useDarkThemeCheckbox.setSelected(true);
		}

		encryptionAlgorithmComboBox.getItems().addAll("AES", "Blowfish");
		encryptionAlgorithmComboBox.getSelectionModel().select(optionHandler.get("encryption_algorithm"));
		dateFormatTextField.setText(optionHandler.get("date_format"));
		timeFormatTextField.setText(optionHandler.get("time_format"));
		autosaveIntervalTextField.setText(optionHandler.get("autosave_interval"));
		twelveHourTimeCheckbox.setSelected(Boolean.valueOf(optionHandler.get("twelve_hour_time")));

		useDarkThemeCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
			optionHandler.set("theme", newValue ? "dark" : "light");
			promptForRestartOnApply = true;
		});

		autosaveIntervalTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			optionHandler.set("autosave_interval", autosaveIntervalTextField.getText());
			promptForRestartOnApply = true;
		});

		twelveHourTimeCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> optionHandler.set("twelve_hour_time", String.valueOf(twelveHourTimeCheckbox.isSelected())));
		dateFormatTextField.focusedProperty().addListener((observable, oldValue, newValue) -> optionHandler.set("date_format", dateFormatTextField.getText()));
		timeFormatTextField.focusedProperty().addListener((observable, oldValue, newValue) -> optionHandler.set("time_format", timeFormatTextField.getText()));
		encryptionAlgorithmComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> optionHandler.set("encryption_algorithm", encryptionAlgorithmComboBox.getValue()));
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
