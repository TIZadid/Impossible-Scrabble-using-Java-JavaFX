package com.company;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class High_Score {
    File file;
    String path;
    ArrayList<Player> PlayerRecords = new ArrayList<>();

    public High_Score(File file, String path) {
        this.file = file;
        this.path = path;
    }

    public void high_score(String name, int score) throws IOException {
        if (!file.exists()) file.createNewFile();
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        //ArrayList<Player> PlayerRecords = new ArrayList<Player>();
        PlayerRecords.add(new Player(name, score));
        int i = 1;
        while ((line = br.readLine()) != null) {
            String[] tokens = StringUtils.split(line);
            String name1 = tokens[0];
            int score1 = Integer.parseInt(tokens[1]);
            Player player1 = new Player(name1, score1);
            PlayerRecords.add(player1);
            //System.out.println(player1.name + " " + player1.score);

        }
        System.out.println("\n");
        Collections.sort(PlayerRecords, new marksCompare());
        FileWriter fileWriter = new FileWriter(path);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (Player player : PlayerRecords) {
            bufferedWriter.write(player.name);

            bufferedWriter.write(" " + player.score);

            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }

    public static void main(String[] args) throws IOException {
        String path = "C:\\Users\\Talha\\Downloads\\The_Project\\src\\com\\company\\High_Score.txt";
        File file = new File(path);
        High_Score high_Score = new High_Score(file, path);
        high_Score.high_score("damra", 4);
    }
}
