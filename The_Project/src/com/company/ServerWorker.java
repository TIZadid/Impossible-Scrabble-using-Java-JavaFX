package com.company;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;


public class ServerWorker extends Thread {

    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private OutputStream outputStream;
    private HashSet<String> topicSet = new HashSet<>();
    String Random;

    public ServerWorker(Server server, Socket clientSocket,String Random) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.Random = Random;
    }

    @Override
    public void run() {
        try {
            //send_all();
            handleClientSocket();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void send_all() throws IOException {
        List<ServerWorker> workerList = server.getWorkerList();
        for(ServerWorker worker : workerList)
        {
            String local_random = "random" + Random;
            outputStream.write(local_random.getBytes());
        }
    }
    private void handleClientSocket() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ( (line = reader.readLine()) != null) {
            System.out.println(line);
            String[] tokens = StringUtils.split(line);
            if (tokens != null && tokens.length > 0) {
                String cmd = tokens[0];
                if ("logoff".equals(cmd) || "quit".equalsIgnoreCase(cmd)) {
                    handleLogoff();
                    break;
                } else if ("login".equalsIgnoreCase(cmd)) {
                    handleLogin(outputStream, tokens);
                } else if ("msg".equalsIgnoreCase(cmd)) {
                    String[] tokensMsg = StringUtils.split(line, null, 3);
                    handleMessage(tokensMsg);
                } else if ("join".equalsIgnoreCase(cmd)) {
                    handleJoin(tokens);
                } else if ("leave".equalsIgnoreCase(cmd)) {
                    handleLeave(tokens);
                } else if ("request".equalsIgnoreCase(cmd)) {
                    handleRequest(tokens);
                }  else if ("noplay".equalsIgnoreCase(cmd)) {
                    handleNoplay(tokens);
                }else if ("play".equalsIgnoreCase(cmd)) {
                    handlePlay(tokens);
                }else if ("cancel".equalsIgnoreCase(cmd)) {
                    handleCancel(tokens);
                }else if ("score".equalsIgnoreCase(cmd)) {
                    handleScore(tokens);
                } else {
                    String msg = "unknown " + cmd + "\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }

        clientSocket.close();
    }

    private void handleScore(String[] tokens) throws IOException {
        if (tokens.length >= 3) {
            String login = tokens[1];
            int score= Integer.parseInt(tokens[2]);
            List<ServerWorker> workerList = server.getWorkerList();
            if (workerList.size() >= 2) {

                for (ServerWorker worker : workerList) {


                    if (!login.equalsIgnoreCase(worker.getLogin())) {
                        String outMsg = "points " + login +" "+score +"\n";
                        worker.send(outMsg);
                        System.out.println(outMsg);

                    }

                }
            }
        }

    }

    private void handleRequest(String [] tokens) throws IOException {
        if (tokens.length >= 2) {
            String login = tokens[1];
            List<ServerWorker> workerList = server.getWorkerList();
            if (workerList.size() >= 2) {

                for (ServerWorker worker : workerList) {


                    if (!login.equalsIgnoreCase(worker.getLogin())) {
                        String outMsg = "invite " + login + "\n";
                        worker.send(outMsg);
                        System.out.println(outMsg);

                    }

                }
            }
        }
    }
    private void handleCancel(String [] tokens) throws IOException {
        if (tokens.length >= 2) {
            String login = tokens[1];
            List<ServerWorker> workerList = server.getWorkerList();
            if (workerList.size() >= 2) {

                for (ServerWorker worker : workerList) {


                    if (!login.equalsIgnoreCase(worker.getLogin())) {
                        String outMsg = "break " + login + "\n";
                        worker.send(outMsg);
                        System.out.println(outMsg);

                    }

                }
            }
        }
    }  private void handlePlay(String [] tokens) throws IOException {
        if (tokens.length >= 2) {
            String login = tokens[1];
            List<ServerWorker> workerList = server.getWorkerList();
            if (workerList.size() >= 2) {

                for (ServerWorker worker : workerList) {


                    if (!login.equalsIgnoreCase(worker.getLogin())) {
                        String outMsg = "start " + login + "\n";
                        worker.send(outMsg);
                        System.out.println(outMsg);

                    }

                }
            }
        }
    }
private void handleNoplay(String [] tokens) throws IOException {
        if (tokens.length >= 2) {
            String login = tokens[1];
            List<ServerWorker> workerList = server.getWorkerList();
            if (workerList.size() >= 2) {

                for (ServerWorker worker : workerList) {


                    if (!login.equalsIgnoreCase(worker.getLogin())) {
                        String outMsg = "end " + login + "\n";
                        worker.send(outMsg);
                        System.out.println(outMsg);

                    }

                }
            }
        }
    }


    private void handleLeave(String[] tokens) {
        if (tokens.length > 1) {
            String topic = tokens[1];
            topicSet.remove(topic);
        }
    }

    public boolean isMemberOfTopic(String topic) {
        return topicSet.contains(topic);
    }

    private void handleJoin(String[] tokens) {
        if (tokens.length > 1) {
            String topic = tokens[1];
            topicSet.add(topic);
        }
    }

    // format: "msg" "login" body...
    // format: "msg" "#topic" body...
    private void handleMessage(String[] tokens) throws IOException {
        String sendTo = tokens[1];
        String body = tokens[2];

        //boolean isTopic = sendTo.charAt(0) == '#';

        List<ServerWorker> workerList = server.getWorkerList();
        for(ServerWorker worker : workerList) {

                if (!sendTo.equalsIgnoreCase(worker.getLogin())) {
                    String outMsg = "msg " + login + " " + body + "\n";
                    worker.send(outMsg);
                }

        }
    }

    private void handleLogoff() throws IOException {
        server.removeWorker(this);
        List<ServerWorker> workerList = server.getWorkerList();

        System.out.println("loggedoff");

        // send other online users current user's status
        String onlineMsg = "offline " + login + "\n";
        for(ServerWorker worker : workerList) {
            if (!login.equals(worker.getLogin())) {
                worker.send(onlineMsg);
            }
        }
       // clientSocket.close();
    }

    public String getLogin() {
        return login;
    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if (tokens.length == 2) {
            String login = tokens[1];



                String msg = "ok login\n";
                outputStream.write(msg.getBytes());
                this.login = login;
                System.out.println("User logged in succesfully: " + login);

                List<ServerWorker> workerList = server.getWorkerList();

                // send current user all other online logins
                for(ServerWorker worker : workerList) {
                    if (worker.getLogin() != null) {
                        if (!login.equals(worker.getLogin())) {
                            String msg2 = "online " + worker.getLogin() + "\n";
                            send(msg2);
                        }
                    }
                }

                // send other online users current user's status
                String onlineMsg = "online " + login + "\n";
                for(ServerWorker worker : workerList) {
                    if (!login.equals(worker.getLogin())) {
                        worker.send(onlineMsg);
                    }
                }
            } else {
                String msg = "error login\n";
                outputStream.write(msg.getBytes());
                System.err.println("Login failed for " + login);
            }
        }


    private void send(String msg) throws IOException {
        if (login != null) {
            outputStream.write(msg.getBytes());
        }
    }
}

