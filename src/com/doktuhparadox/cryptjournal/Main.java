package com.doktuhparadox.cryptjournal;

import com.doktuhparadox.cryptjournal.core.option.OptionManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("core/CryptJournal.fxml"));
        primaryStage.setTitle("CryptJournal");
        primaryStage.setScene(new Scene(root, 1000, 682));
        primaryStage.setResizable(false);
        if (OptionManager.THEME.getValue().equals("dark")) root.getStylesheets().add(getClass().getResource("../../../resources/css/DarkTheme.css").toExternalForm());
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
