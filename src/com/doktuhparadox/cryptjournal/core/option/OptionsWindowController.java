package com.doktuhparadox.cryptjournal.core.option;

import com.doktuhparadox.easel.utils.RuntimeUtils;

import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * Created and written with IntelliJ IDEA.
 * Module of: CryptJournal
 * User: brennanforrest
 * Date of creation: 6/28/14, at 10:32 PM.
 */
public class OptionsWindowController {

    @FXML
    public AnchorPane root;
    @FXML
    private CheckBox useDarkThemeCheckbox;
    @FXML
    private TextField dateFormatTextField;
    @FXML
    private TextField timeFormatTextField;
    @FXML
    private CheckBox twelveHourTimeCheckbox;
    @FXML
    private Button applyButton;


    @FXML
    protected void initialize() {
	    if (OptionManager.optionHandler.get("theme").equals("dark")) {
		    root.getStylesheets().add("/resources/css/DarkTheme.css");
		    useDarkThemeCheckbox.setSelected(true);
	    }
	    dateFormatTextField.setText(OptionManager.optionHandler.get("date_format"));
	    timeFormatTextField.setText(OptionManager.optionHandler.get("time_format"));
	    twelveHourTimeCheckbox.setSelected(Boolean.valueOf(OptionManager.optionHandler.get("twelve_hour_time")));

        useDarkThemeCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (Dialogs.create().masthead(null).title("Restart?").message("Switching themes requires a restart. Would you like to restart?").showConfirm() == Dialog.Actions.YES) {
                this.onApplyButtonPressed();
                RuntimeUtils.restart("CryptJournal.jar");
            }
        });
    }

    @FXML
    public void onApplyButtonPressed() {
	    OptionManager.optionHandler.set("date_format", dateFormatTextField.getText());
	    OptionManager.optionHandler.set("time_format", timeFormatTextField.getText());
	    OptionManager.optionHandler.set("twelve_hour_time", String.valueOf(twelveHourTimeCheckbox.isSelected()));
	    OptionManager.optionHandler.set("theme", useDarkThemeCheckbox.isSelected() ? "dark" : "light");
    }
}
