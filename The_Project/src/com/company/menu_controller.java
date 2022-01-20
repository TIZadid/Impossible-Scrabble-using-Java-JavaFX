package com.company;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;


public class menu_controller {

    static String playerName;

    User loginUser;

    @FXML
    private Button play = new Button();
    @FXML
    private Button exit = new Button();
    @FXML
    private Button highscore = new Button();
    @FXML
    private TextField name = new TextField();


    public void newgame(ActionEvent event) throws IOException {
        playerName = name.getText();
        loginUser = new User(playerName);
        System.out.println(playerName);
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Lobby.fxml"));
        Parent mainWindow = loader.load();

        ClientControllerPlayer control = loader.getController();

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setTitle("Main Frame!");
        Scene XX = new Scene(mainWindow, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());
        //  XX.getStylesheets().add(getClass().getResource("buttonmod.css").toExternalForm());

        window.setScene(XX);
        window.show();
        window.setMaximized(true);
        control.initData(new User(playerName));

    }

    public void highscores(ActionEvent event) throws IOException {
        //  Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("highscore.fxml"));
        Parent mainWindow = loader.load();

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setTitle("Impossible Scrabble");
        Scene XX = new Scene(mainWindow, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());
        window.setScene(XX);
        window.show();
        window.setMaximized(true);

        //   System.out.println(numberOfUsersOnline);
    }
    public void onexitButton()  {



        Stage stage = (Stage) exit.getScene().getWindow();
        stage.close();

    }
}





