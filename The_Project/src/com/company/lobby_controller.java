package com.company;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

public class lobby_controller implements Initializable {
    @FXML
    private ComboBox playtime = new ComboBox();
    @FXML
    private Button play = new Button();


    @FXML
    private ComboBox player = new ComboBox();

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        this.playtime.getItems().removeAll(player.getItems());
        this.playtime.getItems().addAll("100 seconds", "200 seconds", "300 seconds");
        this.playtime.getSelectionModel().select("100 seconds");

        this.player.getItems().removeAll(playtime.getItems());
        this.player.getItems().addAll("1 player", "2 player");
        this.player.getSelectionModel().select("1 player");
    }
}
