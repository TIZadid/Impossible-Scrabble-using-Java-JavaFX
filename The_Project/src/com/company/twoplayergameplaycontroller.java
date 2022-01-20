package com.company;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Stack;

import static com.company.ClientControllerPlayer.numberOfUsersOnline;
import static com.company.menu_controller.playerName;


public class twoplayergameplaycontroller implements Initializable{

    //public int numberOfUsersOnline;
    public int hint=3;
    public int score=0;
    public int lettercount=0;
static String userscore="";
static String oppscore="";
    @FXML private TextArea centralTextArea;
    @FXML private TextArea wordmeaning;
    @FXML private TextArea wordsearchmeaning;

    @FXML private TextField chatBox;
    @FXML private TextField wordBox;
    @FXML private TextField wordsearchBox;
   // @FXML private TextField onlinenumber;
    @FXML private TextField scorebox;
    @FXML private TextField timebox;
    @FXML private TextField hintBox;
    @FXML private Button b1;
    @FXML private Button b2;
    @FXML private Button b3;
    @FXML private Button b4;
    @FXML private Button b5;
    @FXML private Button b6;
    @FXML private Button b7;
    @FXML private Button b8;
    @FXML private Button b9;
    @FXML private Button b10;
    @FXML private Button b11;
    @FXML private Button b12;
    @FXML private Button b13;
    @FXML private Button b14;
    @FXML private Button b15;
    @FXML private Button reset;
    @FXML private Button undo;
    @FXML private Button submit;
    @FXML private Button search;
    @FXML private Button gameover;


    Stack<Integer> stack = new Stack<>();
    private User current;


    private Socket socket;
    private InputStream serverIn;
    private static OutputStream serverOut;
    private BufferedReader bufferedIn;

    Client client;

   // public  int numberOfUsersOnline=1;

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
            //    onlinenumber.clear();
              //  onlinenumber.appendText("Number of players in the Lobby: "+ numberOfUsersOnline);
            }

            @Override
            public void offline(String login) {


                int temp_int=0;
                for(int i=0;i<numberOfUsersOnline;i++){
                    if(currentlyOnline[i].equals(login)) continue;
                    currentlyOnline[temp_int]=currentlyOnline[i];
                    temp_int++;
                }
                numberOfUsersOnline--;
              //  onlinenumber.clear();
            //    onlinenumber.appendText("Number of players in the Lobby: "+ numberOfUsersOnline);


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
                    }else if ("points".equalsIgnoreCase(cmd)) {
                        String tokensMsg = tokens[1];
                        oppscore=tokensMsg;
                        handleScore(oppscore);
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

    private void handleScore(String tokensMsg) {
        gameover.setDisable(false);
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

        socket.close();

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
    public void onClickingonePlayer(ActionEvent event) throws IOException {
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
    }

private static String word="";
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        score=0;
        Random_Generator rand=new Random_Generator();
        String Rand=rand.Random_generator();
        b1.setText(String.valueOf(Rand.charAt(0)));
        b2.setText(String.valueOf(Rand.charAt(1)));
        b3.setText(String.valueOf(Rand.charAt(2)));
        b4.setText(String.valueOf(Rand.charAt(3)));
        b5.setText(String.valueOf(Rand.charAt(4)));
        b6.setText(String.valueOf(Rand.charAt(5)));
        b7.setText(String.valueOf(Rand.charAt(6)));
        b8.setText(String.valueOf(Rand.charAt(7)));
        b9.setText(String.valueOf(Rand.charAt(8)));
        b10.setText(String.valueOf(Rand.charAt(9)));
        b11.setText(String.valueOf(Rand.charAt(10)));
        b12.setText(String.valueOf(Rand.charAt(11)));
        b13.setText(String.valueOf(Rand.charAt(12)));
        b14.setText(String.valueOf(Rand.charAt(13)));
        b15.setText(String.valueOf(Rand.charAt(14)));
        gameover.setVisible(false);
        reset.setDisable(true);
   //     onlinenumber.clear();
       // onlinenumber.appendText("Number of players in the Lobby: "+ numberOfUsersOnline);



        final Integer STARTTIME = 200;
        Timeline timeline = null;
        //Label timerLabel = new Label();
        IntegerProperty timeSeconds = new SimpleIntegerProperty(STARTTIME);


        timebox.textProperty().bind(timeSeconds.asString());





                if (timeline != null) {


                    timeline.stop();

                }
        timeSeconds.addListener((observable, oldTimeValue, newTimeValue) -> {
            // code to execute here...
            // e.g.
            System.out.println("Time left: "+newTimeValue);
            if(newTimeValue.intValue()==0){
                String path = "C:\\Users\\Talha\\Downloads\\The_Project\\src\\com\\company\\High_Score.txt";
               // System.out.println(path);
                File file = new File(path);
                High_Score high_Score = new High_Score(file, path);
                try {
                    high_Score.high_score(playerName,score);
                } catch (IOException e) {
                    e.printStackTrace();
                }
               // score=0;
                b1.setDisable(true);
                b2.setDisable(true);
                b3.setDisable(true);
                b4.setDisable(true);
                b5.setDisable(true);
                b6.setDisable(true);
                b7.setDisable(true);
                b8.setDisable(true);
                b9.setDisable(true);
                b10.setDisable(true);
                b11.setDisable(true);
                b12.setDisable(true);
                b13.setDisable(true);
                b14.setDisable(true);
                b15.setDisable(true);
                reset.setDisable(true);
                undo.setDisable(true);
                submit.setDisable(true);
                search.setDisable(true);
                wordBox.clear();
                wordBox.appendText("GAME OVER");
                gameover.setVisible(true);
                gameover.setDisable(true);
                wordsearchBox.setDisable(true);
                wordmeaning.setDisable(true);
                wordmeaning.setVisible(false);
                wordsearchmeaning.setDisable(true);
                wordsearchBox.setDisable(true);
                userscore=playerName+" "+score;
                String str="score "+playerName+" "+score+"\n";
                System.out.println(str);
                try {
                    serverOut.write(str.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
               // score=0;

            }
        });
                timeSeconds.set(STARTTIME);
                timeline = new Timeline();
                timeline.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(STARTTIME+1),
                                new KeyValue(timeSeconds, 0)));
                timeline.playFromStart();
            }






    public void reset(){

        Random_Generator rand=new Random_Generator();
        String Rand=rand.Random_generator();
        b1.setText(String.valueOf(Rand.charAt(0)));
        b2.setText(String.valueOf(Rand.charAt(1)));
        b3.setText(String.valueOf(Rand.charAt(2)));
        b4.setText(String.valueOf(Rand.charAt(3)));
        b5.setText(String.valueOf(Rand.charAt(4)));
        b6.setText(String.valueOf(Rand.charAt(5)));
        b7.setText(String.valueOf(Rand.charAt(6)));
        b8.setText(String.valueOf(Rand.charAt(7)));
        b9.setText(String.valueOf(Rand.charAt(8)));
        b10.setText(String.valueOf(Rand.charAt(9)));
        b11.setText(String.valueOf(Rand.charAt(10)));
        b12.setText(String.valueOf(Rand.charAt(11)));
        b13.setText(String.valueOf(Rand.charAt(12)));
        b14.setText(String.valueOf(Rand.charAt(13)));
        b15.setText(String.valueOf(Rand.charAt(14)));
        b1.setDisable(false);
        b2.setDisable(false);
        b3.setDisable(false);
        b4.setDisable(false);
        b5.setDisable(false);
        b6.setDisable(false);
        b7.setDisable(false);
        b8.setDisable(false);
        b9.setDisable(false);
        b10.setDisable(false);
        b11.setDisable(false);
        b12.setDisable(false);
        b13.setDisable(false);
        b14.setDisable(false);
        b15.setDisable(false);
        word="";
        wordBox.clear();
        wordBox.appendText(word);
        while (!stack.empty())
        {
            stack.pop();
        }
        reset.setDisable(true);
       lettercount=0;

    }

    public  void  onclickingb1(){
        b1.setDisable(true);
        word+=b1.getText();
        wordBox.clear();
        wordBox.appendText(word);
        stack.push(1);
    }public  void  onclickingb2(){
        b2.setDisable(true);
        word+=b2.getText();
        wordBox.clear();
        wordBox.appendText(word);
        stack.push(2);
    }public void onclickingb3()
    {
        b3.setDisable(true);
        word += b3.getText();
        wordBox.clear();
        wordBox.appendText(word);
        stack.push(3);
    }public void onclickingb4()
    {
        b4.setDisable(true);
        word += b4.getText();
        wordBox.clear();
        wordBox.appendText(word);
        stack.push(4);
    }public void onclickingb5()
    {
        b5.setDisable(true);
        word += b5.getText();
        wordBox.clear();
        wordBox.appendText(word);
        stack.push(5);
    }public void onclickingb6()
    {
        b6.setDisable(true);
        word += b6.getText();
        wordBox.clear();
        wordBox.appendText(word);
        stack.push(6);
    }public void onclickingb7()
    {
        b7.setDisable(true);
        word += b7.getText();
        wordBox.clear();
        wordBox.appendText(word);
        stack.push(7);
    }public void onclickingb8()
    {
        b8.setDisable(true);
        word += b8.getText();
        wordBox.clear();
        wordBox.appendText(word);
        stack.push(8);
    }public void onclickingb9()
    {
        b9.setDisable(true);
        word += b9.getText();
        wordBox.clear();
        wordBox.appendText(word);
        stack.push(9);
    }public void onclickingb10()
    {
        b10.setDisable(true);
        word += b10.getText();
        wordBox.clear();
        wordBox.appendText(word);
        stack.push(10);
    }
    public void onclickingb11()
    {
        b11.setDisable(true);
        word += b11.getText();
        wordBox.clear();
        wordBox.appendText(word);
        stack.push(11);
    }
    public void onclickingb12()
    {
        b12.setDisable(true);
        word += b12.getText();
        wordBox.clear();
        wordBox.appendText(word);
        stack.push(12);
    }
    public void onclickingb13()
    {
        b13.setDisable(true);
        word += b13.getText();
        wordBox.clear();
        wordBox.appendText(word);
        stack.push(13);
    }public void onclickingb14()
    {
        b14.setDisable(true);
        word += b14.getText();
        wordBox.clear();
        wordBox.appendText(word);
        stack.push(14);
    }public void onclickingb15()
    {
        b15.setDisable(true);
        word += b15.getText();
        wordBox.clear();
        wordBox.appendText(word);
        stack.push(15);
    }





    public void onpressingundo(){
        word="";
        wordBox.clear();
        while(!stack.empty()){
            int x=stack.peek();
            stack.pop();
            if(x==1)b1.setDisable(false);
            else if(x==2)b2.setDisable(false);
            else if(x==3)b3.setDisable(false);
            else if(x==4)b4.setDisable(false);
            else if(x==5)b5.setDisable(false);
            else if(x==6)b6.setDisable(false);
            else if(x==7)b7.setDisable(false);
            else if(x==8)b8.setDisable(false);
            else if(x==9)b9.setDisable(false);
            else if(x==10)b10.setDisable(false);
            else if(x==11)b11.setDisable(false);
            else if(x==12)b12.setDisable(false);
            else if(x==13)b13.setDisable(false);
            else if(x==14)b14.setDisable(false);
            else if(x==15)b15.setDisable(false);

        }


    }
  public  void onpressingsubmit() throws IOException, SAXException, ParserConfigurationException {
      ApI apI = new ApI();
      String searched_word = apI.Search_word(word);
wordBox.clear();
      wordmeaning.clear();
int l=word.length();
lettercount+=l;
if(lettercount>4)
    reset.setDisable(false);
      if(searched_word.length()>0) {

score+=l*l;
scorebox.clear();
scorebox.appendText("Score: "+score);
          wordmeaning.appendText(searched_word);
      }
      else
      {
          score-=5;
          scorebox.clear();
          scorebox.appendText("Score: "+score);
          //wordmeaning.clear();
          wordmeaning.appendText("word does not exist");
      }
      while (!stack.empty())
      {
          stack.pop();
      }
      word="";
  }
  public  void onclickingsearch() throws IOException, SAXException, ParserConfigurationException,NullPointerException {
      if (wordsearchBox.getText().length() > 0) {
          String str = wordsearchBox.getText();
          String[] tokens = StringUtils.split(str);
          if (tokens != null && tokens.length > 0) {
              ApI apI = new ApI();

              String searched_word = apI.Search_word(wordsearchBox.getText());

              wordsearchmeaning.clear();

              if (searched_word.length() > 0) {


                  wordsearchmeaning.appendText(searched_word);
              } else {
                  //wordmeaning.clear();
                  wordsearchmeaning.appendText("word does not exist");
              }
              wordsearchBox.clear();
              hint--;
              hintBox.clear();
              hintBox.setText("Hints Left: " + hint);
              if (hint == 0)
                  search.setDisable(true);


          }
      }
  }
    public void onGameover(ActionEvent event) throws IOException {
        //  Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
        String str="score "+playerName+" "+score+"\n";
        System.out.println(str);
        try {
            serverOut.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("result.fxml"));
        Parent mainWindow = loader.load();

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setTitle("Impossible Scrabble");
        Scene XX = new Scene(mainWindow, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());
        window.setScene(XX);
        window.show();
        window.setMaximized(true);

        //   System.out.println(numberOfUsersOnline);
    }
}