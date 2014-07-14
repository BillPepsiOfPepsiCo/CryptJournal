package com.doktuhparadox.cryptjournal.core.option;

import com.doktuhparadox.easel.utils.RuntimeUtils;

import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.File;

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
	private CheckBox useDarkThemeCheckbox;
	@FXML
	private TextField autosaveIntervalTextField;
	@FXML
	private TextField dateFormatTextField;
    @FXML
    private TextField timeFormatTextField;
    @FXML
    private CheckBox twelveHourTimeCheckbox;
    @FXML
    private Button applyButton;

	private static final File optionsFile = new File("Options.txt");

	@FXML
	protected void initialize() {
		if (optionHandler.get("theme").equals("dark")) {
		    root.getStylesheets().add("/resources/css/DarkTheme.css");
		    useDarkThemeCheckbox.setSelected(true);
	    }
		dateFormatTextField.setText(optionHandler.get("date_format"));
		timeFormatTextField.setText(optionHandler.get("time_format"));
		autosaveIntervalTextField.setText(optionHandler.get("autosave_interval"));
		twelveHourTimeCheckbox.setSelected(Boolean.valueOf(optionHandler.get("twelve_hour_time")));
	}

    @FXML
    public void onApplyButtonPressed() {
	    optionHandler.set("date_format", dateFormatTextField.getText());
	    optionHandler.set("time_format", timeFormatTextField.getText());
	    optionHandler.set("twelve_hour_time", String.valueOf(twelveHourTimeCheckbox.isSelected()));
	    optionHandler.set("theme", useDarkThemeCheckbox.isSelected() ? "dark" : "light");
	    optionHandler.set("autosave_interval", autosaveIntervalTextField.getText());
	    if (Dialogs.create().masthead(null).title("Restart?").message("Applying these options requires a restart. Would you like to restart?").showConfirm() == Dialog.Actions.YES) {
		    RuntimeUtils.restart("CryptJournal.jar");
	    }
    }
}
