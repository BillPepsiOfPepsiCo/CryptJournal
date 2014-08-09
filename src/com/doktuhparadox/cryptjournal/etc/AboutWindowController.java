package com.doktuhparadox.cryptjournal.etc;

import com.doktuhparadox.cryptjournal.Main;
import com.doktuhparadox.easel.utils.NetUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created and written with IntelliJ IDEA CE 14.
 * Package: com.doktuhparadox.cryptjournal.etc
 * Module of: CryptJournal
 * Author: Brennan Forrest (DoktuhParadox)
 * Date of creation: 7/18/14 at 8:13 PM.
 */
public class AboutWindowController {
    @FXML
    private Button openGitHubButton;
    @FXML
    private Button openTwitterButton;
    @FXML
    private Label versionLabel;

    @FXML
    void initialize() {
        versionLabel.setText("Version " + Main.version);

        openGitHubButton.setOnAction(event -> {
            try {
                NetUtils.openWebpage(new URL("http://github.com/DoktuhParadox/CryptJournal"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        });

        openTwitterButton.setOnAction(event -> {
            try {
                NetUtils.openWebpage(new URL("http://www.twitter.com/DoktuhParadox"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        });
    }
}
