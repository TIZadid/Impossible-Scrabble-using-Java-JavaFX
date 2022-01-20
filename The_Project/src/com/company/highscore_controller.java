package com.company;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class highscore_controller implements Initializable
{
    @FXML private TextField one;
    @FXML private TextField two;
    @FXML private TextField three;
    @FXML private TextField four;
    @FXML private TextField five;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String path = "C:\\Users\\Talha\\Downloads\\The_Project\\src\\com\\company\\High_Score.txt";
        File file = new File(path);
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line,line1,line2,line3,line4;
            line = "1: "+bufferedReader.readLine();
            line1 = "2: "+bufferedReader.readLine();
            line2 = "3: "+bufferedReader.readLine();
            line3 = "4: "+bufferedReader.readLine();
            line4 = "5: "+bufferedReader.readLine();
            one.appendText(line);
            two.appendText(line1);
            three.appendText(line2);
            four.appendText(line3);
            five.appendText(line4);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button Back;


    public void onClickingBack(ActionEvent event) throws IOException {
        //  Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("startscreen.fxml"));
        Parent mainWindow = loader.load();

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setTitle("Impossible Scrabble");
        Scene XX = new Scene(mainWindow, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());
        window.setScene(XX);
        window.show();
        window.setMaximized(true);
    }
}
