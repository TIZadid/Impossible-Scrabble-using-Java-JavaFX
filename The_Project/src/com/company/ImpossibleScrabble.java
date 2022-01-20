package com.company;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class ImpossibleScrabble extends Application{

    public static void main(String args[]) throws IOException {
        launch(args);

    }

    public void start(Stage primaryStage) throws Exception{


        Parent root = FXMLLoader.load(getClass().getResource("startscreen.fxml"));
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setTitle("Impossible Scrabble");
        primaryStage.setScene(new Scene(root, primaryScreenBounds.getWidth(),primaryScreenBounds.getHeight()));
        primaryStage.show();

    }

}
