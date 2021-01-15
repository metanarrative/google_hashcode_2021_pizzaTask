package xyz.metanarrative;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws Exception{
        File file = new File("b_little_bit_of_everything.in");

        RandomAccessFile fileReader = new RandomAccessFile(file, "r");

        String[] lineVars = fileReader.readLine().split(" ");

        int pizzaAmount           = getLineVar(lineVars, 0);
        int twoPersonTeamAmount   = getLineVar(lineVars, 1);
        int threePersonTeamAmount = getLineVar(lineVars, 2);
        int fourPersonTeamAmount  = getLineVar(lineVars, 3);

        ArrayList<String[]> pizzas = new ArrayList<>();
        getPizzasFromFile(fileReader, pizzas);
        countPizzas(pizzas);

    }

    static void getPizzasFromFile(RandomAccessFile fileReader, ArrayList<String[]> pizzas){
        try {
            while (true){
                String[] pizzaLine = fileReader.readLine().split(" ");
                pizzas.add(pizzaLine);
            }
        }
        catch (Exception e){
            //file ended
        }
    }

    static int getLineVar(String[] lineVars, int pos){
        return Integer.parseInt(lineVars[pos]);
    }

    static void countPizzas(ArrayList<String[]> pizzas){
        int small = 0;
        int medium = 0;
        int large = 0;
        for (String[] arr : pizzas
        ) {
            int num = Integer.parseInt(arr[0]);
            if(num > 0 & num <= 4){
                small++;
            }
            if(num > 4 & num <= 7){
                medium++;
            }
            if(num > 7 & num <= 10){
                large++;
            }
        }
        System.out.println("Small pizza amount: " + small);
        System.out.println("Medium pizza amount: "+ medium);
        System.out.println("Large pizza amount: " + large);

    }

}
