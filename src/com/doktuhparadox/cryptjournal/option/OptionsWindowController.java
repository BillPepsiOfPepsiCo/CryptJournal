package com.doktuhparadox.cryptjournal.option;

import com.doktuhparadox.cryptjournal.core.EncryptionAlgorithm;
import com.doktuhparadox.easel.utils.RuntimeUtils;

import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.doktuhparadox.cryptjournal.option.OptionsManager.optionHandler;
import resources.Index;

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
	private ComboBox<String> paddingComboBox;
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

	private boolean promptForRestartOnApply = false;

	@FXML
	protected void initialize() {
		root.getStylesheets().add(Index.rootTweaksStylesheet.toExternalForm());

		if (optionHandler.get("theme").equals("dark")) {
			root.getStylesheets().add(Index.darkThemeStylesheet.toExternalForm());
			useDarkThemeCheckbox.setSelected(true);
		}

		dateFormatTextField.setFont(Font.loadFont(this.getClass().getResourceAsStream("/resources/font/mplus-1m-regular.ttf"), 12));
		timeFormatTextField.setFont(Font.loadFont(this.getClass().getResourceAsStream("/resources/font/mplus-1m-regular.ttf"), 12));
		autosaveIntervalTextField.setFont(Font.loadFont(this.getClass().getResourceAsStream("/resources/font/mplus-1m-regular.ttf"), 12));

		dateFormatTextField.setText(optionHandler.get("date_format"));
		timeFormatTextField.setText(optionHandler.get("time_format"));
		autosaveIntervalTextField.setText(optionHandler.get("autosave_interval"));

		String[] encryptionAlgorithmAndPadding = optionHandler.get("encryption_algorithm").split("/");
		encryptionAlgorithmComboBox.getItems().addAll(Arrays.asList(EncryptionAlgorithm.values()).stream().map(Enum::toString).collect(Collectors.toList()));
		paddingComboBox.getItems().addAll("None", "CBC NoPadding", "CBC PKCS5Padding", "ECB NoPadding", "CBC PKCS5Padding");
		encryptionAlgorithmComboBox.getSelectionModel().select(encryptionAlgorithmAndPadding[0]);

		if (encryptionAlgorithmAndPadding.length > 1)
			paddingComboBox.getSelectionModel().select(encryptionAlgorithmAndPadding[1] + " " + encryptionAlgorithmAndPadding[2]);
		else paddingComboBox.getSelectionModel().select("None");

		encryptionAlgorithmComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.equals("Blowfish")) {
				paddingComboBox.setDisable(true);
				paddingComboBox.getSelectionModel().select(-1);
			} else {
				paddingComboBox.setDisable(false);
				paddingComboBox.getSelectionModel().select(0);
			}
		});

		useDarkThemeCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
			optionHandler.set("theme", newValue ? "dark" : "light");
			promptForRestartOnApply = true;
		});

		autosaveIntervalTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			optionHandler.set("autosave_interval", autosaveIntervalTextField.getText());
			promptForRestartOnApply = true;
		});

		ChangeListener<String> changeListener = (observable, oldValue, newValue) -> {
			String s = paddingComboBox.getValue();
			if (s == null || s.equals("None")) {
				optionHandler.set("encryption_algorithm", encryptionAlgorithmComboBox.getValue());
			} else {
				optionHandler.set("encryption_algorithm", encryptionAlgorithmComboBox.getValue() + "/" + s);
			}
		};

		cachePasswordsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> optionHandler.set("cache_passwords", "true"));
		dateFormatTextField.focusedProperty().addListener((observable, oldValue, newValue) -> optionHandler.set("date_format", dateFormatTextField.getText()));
		timeFormatTextField.focusedProperty().addListener((observable, oldValue, newValue) -> optionHandler.set("time_format", timeFormatTextField.getText()));
		encryptionAlgorithmComboBox.getSelectionModel().selectedItemProperty().addListener(changeListener);
		paddingComboBox.getSelectionModel().selectedItemProperty().addListener(changeListener);
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
