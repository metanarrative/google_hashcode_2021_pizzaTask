package xyz.metanarrative;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.*;

public class Main {

    static boolean leftNeighbour = false;

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

        LinkedList<String[]> sorted = new LinkedList<>(pizzas);
        sorted.sort(Comparator.comparingInt(o -> Integer.parseInt(o[0])));

        LinkedList<String[]> sortedWithoutNums =  removeNums(sorted);

        LinkedList<String> ingridients = createFullIngridientsList(sortedWithoutNums);

        for (String s:ingridients
             ) {
            System.out.println(s);
        }

    }

    static LinkedList<String[]> removeNums(LinkedList<String[]> old){
        LinkedList<String[]> result = new LinkedList<>();

        LinkedList<String[]> sortedWithoutNums = new LinkedList<>(old);
        for (String[] arr:sortedWithoutNums
             ) {
            LinkedList<String> arrToList = new LinkedList<>(Arrays.asList(arr));
            arrToList.remove(0);
            String[] newArr = new String[arrToList.size()];
            for (int i = 0; i < newArr.length; i++) {
                newArr[i] = arrToList.get(i);
            }
            result.add(newArr);
        }
        return result;
    }

    static LinkedList<String> createFullIngridientsList(LinkedList<String[]> sortedPizzas){
        LinkedList<String[]> sortedPizzasCopy = new LinkedList<>(sortedPizzas);
        LinkedList<String> ingridients = new LinkedList<>(Arrays.asList(sortedPizzasCopy.getLast()));

        while (sortedPizzasCopy.size() > 1) {
//            LinkedList<String> bigger = new LinkedList<>(Arrays.asList(sortedPizzasCopy.removeLast()));
            sortedPizzasCopy.removeLast();
            //lesserDiff is what is present in lesser, but absent in bigger
            LinkedList<String> lesserDiff = new LinkedList<>(Arrays.asList(sortedPizzasCopy.getLast()));
            lesserDiff.removeAll(ingridients);

            if (lesserDiff.size() > 0) {
                LinkedList<String> lesser = new LinkedList<>(Arrays.asList(sortedPizzasCopy.removeLast()));

                for (String ingr : lesserDiff
                ) {
                    int ingrID = lesser.indexOf(ingr);  //id of ingr in lesser that is absent in bigger

                    //get ingr neighbours in lesser
                    String neighbour = findLesserNeighbours(ingrID, lesser, 1);
                    System.out.println("Neighbour: " + neighbour);

                    if (neighbour == null) {
                        return ingridients;
                    }

                    int ingrIdInIngridients = 0;
                    if (Main.leftNeighbour) {
                        ingrIdInIngridients = ingridients.indexOf(neighbour) + 1;
                    } else if ((ingridients.indexOf(neighbour) - 1) > -1) {
                        ingrIdInIngridients = ingridients.indexOf(neighbour) - 1;
                    }
                    ingridients.add(ingrIdInIngridients, ingr);
                }
            }
        }
        return ingridients;
    }

    static String findLesserNeighbours(int ingrID, LinkedList<String> lesser, int searchrange){
        String leftNeighbour;
        String rightNeighbour;
        if(ingrID - searchrange > -1 ) {
            leftNeighbour = lesser.get(ingrID - 1);
            Main.leftNeighbour = true;
            return leftNeighbour;
        }
        if(ingrID + searchrange <= lesser.size() & ingrID+1 <= lesser.size() - 1){
            rightNeighbour = lesser.get(ingrID + 1);
            Main.leftNeighbour = false;
            return rightNeighbour;
        }
        //else findLesserNeighbours(ingrID, lesser, ++searchrange);
        return null;
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

}
