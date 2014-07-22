package com.doktuhparadox.cryptjournal;

import com.doktuhparadox.cryptjournal.core.JournalEntry;
import com.doktuhparadox.cryptjournal.core.MacAppModule;
import com.doktuhparadox.cryptjournal.option.OptionsManager;
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

import resources.Index;

public class Main extends Application implements IPlatformDifferentiator {

    public static final String version = "1.0";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
	    OptionsManager.initialize();
	    PlatformDifferentiator.setPlatformDifferentiator(this);
	    Parent root = FXMLLoader.load(getClass().getResource("core/CryptJournal.fxml"));
        primaryStage.setTitle("CryptJournal");
        primaryStage.setScene(new Scene(root, 1000, 682));
	    primaryStage.initStyle(StageStyle.UNIFIED);
	    primaryStage.setResizable(false);
	    root.getStylesheets().add(Index.rootTweaksStylesheet.toExternalForm());
	    if (OptionsManager.optionHandler.get("theme").equals("dark"))
		    root.getStylesheets().add(Index.darkThemeStylesheet.toExternalForm());
	    primaryStage.show();
    }

    @Override
    public void ifMac() {
	    com.apple.eawt.Application app = com.apple.eawt.Application.getApplication();
	    app.setDockIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/img/Icon.png")));
	    app.setAboutHandler(new MacAppModule());
	    System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
	    System.setProperty("apple.laf.useScreenMenuBar", "true");
	    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "CryptJournal");
    }

    @Override
    public void ifWindows() {
	    try {
		    Files.setAttribute(JournalEntry.infoDir.toPath(), "dos:hidden", true); //Hide the .metadata folder on Windows
	    } catch (IOException e) {
		    e.printStackTrace();
	    }
    }

    @Override
    public void ifLinux() {

    }

    @Override
    public void ifSolaris() {

    }
}
