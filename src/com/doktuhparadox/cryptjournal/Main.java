package com.doktuhparadox.cryptjournal;

import com.doktuhparadox.cryptjournal.core.option.OptionManager;
import com.doktuhparadox.easel.platform.IPlatformDifferentiator;
import com.doktuhparadox.easel.platform.PlatformDifferentiator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application implements IPlatformDifferentiator {

    @Override
    public void start(Stage primaryStage) throws Exception {
        PlatformDifferentiator.setPlatformDifferentiator(this);
        Parent root = FXMLLoader.load(getClass().getResource("core/CryptJournal.fxml"));
        primaryStage.setTitle("CryptJournal");
        primaryStage.setScene(new Scene(root, 1000, 682));
        primaryStage.setResizable(false);
        if (OptionManager.THEME.getValue().equals("dark"))
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

    }

    @Override
    public void ifLinux() {

    }

    @Override
    public void ifSolaris() {

    }
}
