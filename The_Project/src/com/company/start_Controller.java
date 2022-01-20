package com.company;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class start_Controller {

    @FXML
    private Button start;


    public void onClickingStart(ActionEvent event) throws IOException {
        //  Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("menu.fxml"));
        Parent mainWindow = loader.load();

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setTitle("Impossible Scrabble");
        Scene XX = new Scene(mainWindow, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());
        window.setScene(XX);
        window.show();
        window.setMaximized(true);
    }
}




