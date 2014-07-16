package com.doktuhparadox.cryptjournal;

import com.doktuhparadox.cryptjournal.core.JournalEntry;
import com.doktuhparadox.cryptjournal.core.option.OptionsManager;
import com.doktuhparadox.easel.platform.IPlatformDifferentiator;
import com.doktuhparadox.easel.platform.PlatformDifferentiator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.Toolkit;
import java.io.IOException;
import java.nio.file.Files;

public class Main extends Application implements IPlatformDifferentiator {

    @Override
    public void start(Stage primaryStage) throws Exception {
	    OptionsManager.initialize();
	    PlatformDifferentiator.setPlatformDifferentiator(this);
	    Parent root = FXMLLoader.load(getClass().getResource("core/CryptJournal.fxml"));
        primaryStage.setTitle("CryptJournal");
        primaryStage.setScene(new Scene(root, 1000, 682));
	    primaryStage.initStyle(StageStyle.UNIFIED);
	    primaryStage.setResizable(false);
	    root.getStylesheets().add(getClass().getResource("/resources/css/RootStyle.css").toExternalForm());
	    if (OptionsManager.optionHandler.get("theme").equals("dark"))
		    root.getStylesheets().add(getClass().getResource("/resources/css/DarkTheme.css").toExternalForm());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void ifMac() {
        com.apple.eawt.Application.getApplication().setDockIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/img/Icon.png")));
    }

    @Override
    public void ifWindows() {
	    if (JournalEntry.infoDir.exists()) {
		    try {
			    Files.setAttribute(JournalEntry.infoDir.toPath(), "dos:hidden", true); //Hide the .metadata folder on Windows
		    } catch (IOException e) {
			    e.printStackTrace();
		    }
	    }
    }

    @Override
    public void ifLinux() {

    }

    @Override
    public void ifSolaris() {

    }
}
