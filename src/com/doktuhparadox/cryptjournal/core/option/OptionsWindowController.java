package com.doktuhparadox.cryptjournal.core.option;

import com.doktuhparadox.easel.utils.RuntimeUtils;

import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

/**
 * Created and written with IntelliJ IDEA.
 * Module of: CryptJournal
 * User: brennanforrest
 * Date of creation: 6/28/14, at 10:32 PM.
 */
public class OptionsWindowController {

    @FXML
    public CheckBox useDarkThemeCheckbox;
    @FXML
    public TextField dateFormatTextField;
    @FXML
    public TextField timeFormatTextField;
    @FXML
    public CheckBox twelveHourTimeCheckbox;
    @FXML
    public Button applyButton;


    @FXML
    protected void initialize() {
        dateFormatTextField.setText(OptionManager.DATE_FORMAT.getValue());
        timeFormatTextField.setText(OptionManager.TIME_FORMAT.getValue());
        useDarkThemeCheckbox.setSelected(OptionManager.THEME.getValue().equals("dark"));
        twelveHourTimeCheckbox.setSelected(Boolean.valueOf(OptionManager.TWELVE_HOUR_TIME.getValue()));

        useDarkThemeCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (Dialogs.create().masthead(null).title("Restart?").message("Switching themes requires a restart. Would you like to restart?").showConfirm() == Dialog.Actions.YES)
                RuntimeUtils.restart("CryptJournal.jar");

            this.onApplyButtonPressed();
        });
    }

    @FXML
    public void onApplyButtonPressed() {
        OptionManager.DATE_FORMAT.setValue(dateFormatTextField.getText());
        OptionManager.TIME_FORMAT.setValue(timeFormatTextField.getText());
        OptionManager.TWELVE_HOUR_TIME.setValue(String.valueOf(twelveHourTimeCheckbox.isSelected()));
        OptionManager.THEME.setValue(useDarkThemeCheckbox.isSelected() ? "dark" : "light");
    }
}
