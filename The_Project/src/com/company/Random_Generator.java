package com.company;

public class Random_Generator {
    String randomizer;
    int random;
    public Random_Generator(){
        this.randomizer = "";
        random = 0;
    }
    public String Random_generator()
    {
        char a;
        for(int i=0;i<15;i++)
        {
            random =(int) (Math.random()*100)%26;
            a =(char) (random+65);
            randomizer +=a;
        }
        System.out.println(randomizer);

        //System.out.println(randomizer);\
        return randomizer;
    }

   /* public static void main(String[] args) {
        Random_Generator random_generator = new Random_Generator();
        String random1 = random_generator.Random_generator();
        System.out.println(random1);
    }*/
}
