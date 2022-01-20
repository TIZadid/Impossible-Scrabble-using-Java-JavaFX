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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static com.company.menu_controller.playerName;

public class ClientControllerPlayer implements Initializable{

    @FXML private TextArea centralTextArea;

    @FXML private TextField chatBox;
   // @FXML private TextField onlinenumber;
    @FXML private Button onePlayer;
    @FXML private Button twoPlayer;
    @FXML private Button yes;
    @FXML private Button no;
    @FXML private Button start;
    @FXML private Text invite;
    @FXML private Text request;
    @FXML private Button cancel;


    private User current;


    private Socket socket;
    private InputStream serverIn;
    private static OutputStream serverOut;
    private BufferedReader bufferedIn;

    Client client;

    public  static  int numberOfUsersOnline=1;

    public String[] currentlyOnline= new String[200];


    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();

    public void initData(User current) throws IOException {

        client = new Client("localhost",8818);
        this.current=current;

        this.addUserStatusListener(new UserStatusListener() {
            @Override
            public void online(String login) {




                currentlyOnline[numberOfUsersOnline] = new String();
                currentlyOnline[numberOfUsersOnline] = login;
                numberOfUsersOnline++;
           //     onlinenumber.clear();
             //   onlinenumber.appendText("Number of players in the Lobby: "+ numberOfUsersOnline);
            }

            @Override
            public void offline(String login) {

                System.out.println(numberOfUsersOnline+" "+currentlyOnline.length+" "+login);
                int temp_int=0;
                for(int i=0;i<numberOfUsersOnline;i++){
                    if(currentlyOnline[i].equals(login)) continue;
                    currentlyOnline[temp_int]=currentlyOnline[i];
                    temp_int++;
                }
                numberOfUsersOnline--;
         //       onlinenumber.clear();
           //     onlinenumber.appendText("Number of players in the Lobby: "+ numberOfUsersOnline);
            }
        });

        this.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(String fromLogin, String msgBody) {
                centralTextArea.appendText(fromLogin+" : "+msgBody+"\n");
            }
        });

        if (!this.connect()) {
            System.err.println("Connect failed.");
        }
        else {
            System.out.println("Connect successful");

            if (login(current.user)) {
                System.out.println("Login successful");


            } else {
                System.err.println("Login failed");
            }

        }

    }

    private void handleMessage(String tokensMsg) {
        String[] tokens=StringUtils.split(tokensMsg,null,2);
        String login = tokens[0];
        String msgBody = tokens[1];
        System.out.println(msgBody+" ~ "+tokensMsg);

        for(MessageListener listener : messageListeners) {
            listener.onMessage(login, msgBody);
        }
    }

    private void handleOffline(String tokens) {
        String login = tokens;
        for(UserStatusListener listener : userStatusListeners) {
            listener.offline(login);
        }
    }

    private void handleOnline(String tokens) {
        String login = tokens;
        System.out.println("~ "+userStatusListeners.size());
        for(UserStatusListener listener : userStatusListeners) {
            System.out.println("jabe online e " + login);
            listener.online(login);
        }
    }

    private void startMessageReader() {
        Thread t = new Thread() {
            @Override
            public void run() {
                if(!socket.isClosed()) readMessageLoop();
                else return;
            }
        };
        t.start();
    }

    private void readMessageLoop() {
        try {
            String line;
            while (!socket.isClosed() && ((line = bufferedIn.readLine()) != null)) {
                System.out.println("~ "+line);
                String[] tokens = StringUtils.split(line,null,2);
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if ("online".equalsIgnoreCase(cmd)) {
                        handleOnline(tokens[1]);
                    } else if ("offline".equalsIgnoreCase(cmd)) {
                        handleOffline(tokens[1]);
                    } else if ("msg".equalsIgnoreCase(cmd)) {
                        String tokensMsg = tokens[1];
                        handleMessage(tokensMsg);
                    }else if ("invite".equalsIgnoreCase(cmd)) {
                        String tokensMsg = tokens[1];
                        handleInvite(tokensMsg);
                    }else if ("end".equalsIgnoreCase(cmd)) {

                        String tokensMsg = tokens[1];
                        System.out.println("end "+tokensMsg);
                        handleDeny(tokensMsg);
                    }else if ("start".equalsIgnoreCase(cmd)) {

                        String tokensMsg = tokens[1];
                        System.out.println("start "+tokensMsg);
                        handleStart(tokensMsg);
                    }else if ("break".equalsIgnoreCase(cmd)) {

                        String tokensMsg = tokens[1];
                        System.out.println("break "+tokensMsg);
                        handleBreak(tokensMsg);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInvite(String tokens) {

       invite.setVisible(true);
        yes.setVisible(true);
        no.setVisible(true);
        onePlayer.setVisible(false);
        twoPlayer.setVisible(false);
        request.setVisible(false);

    }
    private void handleBreak(String tokens) {

        request.setVisible(false);
        onePlayer.setVisible(true);
        twoPlayer.setVisible(true);
        start.setVisible(false);
        //start.setDisable(true);
        cancel.setVisible(false);
        invite.setVisible(false);
        yes.setVisible(false);
        no.setVisible(false);

    }private void handleStart(String tokens) {

       invite.setVisible(false);
        yes.setVisible(false);
        no.setVisible(false);
        onePlayer.setVisible(false);
        twoPlayer.setVisible(false);
        request.setVisible(true);
        start.setVisible(true);
        start.setDisable(false);
        cancel.setDisable(true);

    }
    private void handleDeny(String tokens) {

       invite.setVisible(false);
        yes.setVisible(false);
        no.setVisible(false);
        request.setVisible(false);
        invite.setVisible(false);
        onePlayer.setVisible(true);
        twoPlayer.setVisible(true);
        start.setVisible(false);

    }



    public boolean login(String login) throws IOException {
        String cmd = "login " + login + "\n";
        serverOut.write(cmd.getBytes());

        String response = bufferedIn.readLine();
        System.out.println("Response Line:" + response);

        if ("ok login".equalsIgnoreCase(response)) {
            startMessageReader();
            return true;
        } else {
            return false;
        }
    }


    public boolean connect() {
        try {
            this.socket = new Socket(client.serverName, client.serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void onCloseButton() throws IOException {
        String str = "logoff "+ current.user;

        try{
            serverOut.write(str.getBytes());
        }catch(NullPointerException e){
           // Stage stage = (Stage) closeButton.getScene().getWindow();
          //  stage.close();
            return;
        }

       // socket.close();

      //  Stage stage = (Stage) closeButton.getScene().getWindow();

     //   stage.close();

    }

    public static void msg(String msgBody) throws IOException {
        String cmd = msgBody + "\n";
        System.out.println("Chikan "+msgBody);
        serverOut.write(cmd.getBytes());
    }

    public void onPressingSend() throws IOException {

        //String[] tokens = StringUtils.split(chatBox.getText());
        if (chatBox.getText().length()>0 )
        {
            String str="msg "+current.user+" "+chatBox.getText();
            String[] tokens = StringUtils.split(str);
            if (tokens != null && tokens.length > 2)
            {
                printOnTextField(chatBox.getText());
            System.out.println("~ "+chatBox.getText()+" "+chatBox.getText().length());
            chatBox.clear();
            //String[] tokenMsg = StringUtils.split(str,null,2);
            try {
                msg(str);
            } catch (IOException e) {
                e.printStackTrace();
            }}
        else chatBox.clear();}
    }



    public void logoff() throws IOException {
        String cmd = "logoff\n";
        serverOut.write(cmd.getBytes());
    }

    public void printOnTextField(String str){
        centralTextArea.appendText(str+"\n");
    }

    public void addUserStatusListener(UserStatusListener listener) {
        userStatusListeners.add(listener);
    }

    public void removeUserStatusListener(UserStatusListener listener) {
        userStatusListeners.remove(listener);
    }

    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }
    public void onClickingonePlayer(ActionEvent event) throws IOException, InterruptedException {
        //  Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));



        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("GamePlay.fxml"));
        Parent mainWindow = loader.load();

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setTitle("Impossible Scrabble");
        Scene XX = new Scene(mainWindow, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());
        window.setScene(XX);
        window.show();
        window.setMaximized(true);
        numberOfUsersOnline-=2;
        gameplaycontroller control = loader.getController();
        control.initData(new User(playerName));
    }

    public void onClickingtwoPlayer(ActionEvent event) throws IOException {
        String str="request "+playerName+"\n";
        serverOut.write(str.getBytes());
        request.setVisible(true);
        onePlayer.setVisible(false);
        twoPlayer.setVisible(false);
        start.setVisible(true);
        start.setDisable(true);
        cancel.setVisible(true);
      //  yes.setVisible(true);

      //  no.setVisible(true);
    }
    public void onClickingCancel(ActionEvent event) throws IOException {
        String str="cancel "+playerName+"\n";
        serverOut.write(str.getBytes());
        request.setVisible(false);
        onePlayer.setVisible(true);
        twoPlayer.setVisible(true);
        start.setVisible(false);
        //start.setDisable(true);
        cancel.setVisible(false);
        invite.setVisible(false);
        yes.setVisible(false);
        no.setVisible(false);
      //  yes.setVisible(true);

      //  no.setVisible(true);
    }

    public void onClickingNo(ActionEvent event) throws IOException {
        String str="noplay "+playerName+"\n";
        System.out.println(str);
        serverOut.write(str.getBytes());
        request.setVisible(false);
        invite.setVisible(false);
        onePlayer.setVisible(true);
        twoPlayer.setVisible(true);
        yes.setVisible(false);
        no.setVisible(false);
        cancel.setVisible(false);
        //  yes.setVisible(true);

        //  no.setVisible(true);
    }
    public void onClickingYes(ActionEvent event) throws IOException {

        String str="play "+playerName+"\n";
        System.out.println(str);
        serverOut.write(str.getBytes());
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("twoplayergameplay.fxml"));
        Parent mainWindow = loader.load();

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setTitle("Impossible Scrabble");
        Scene XX = new Scene(mainWindow, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());
        window.setScene(XX);
        window.show();
        window.setMaximized(true);
        numberOfUsersOnline-=2;
        twoplayergameplaycontroller control = loader.getController();
        control.initData(new User(playerName));
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
yes.setVisible(false);
start.setVisible(false);
no.setVisible(false);
invite.setVisible(false);
request.setVisible(false);
cancel.setVisible(false);
    }
}
