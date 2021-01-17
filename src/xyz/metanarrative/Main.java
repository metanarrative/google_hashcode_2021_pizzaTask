package xyz.metanarrative;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.*;

public class Main {
    static int pizzaAmount           = 0;
    static int twoPersonTeamAmount   = 0;
    static int threePersonTeamAmount = 0;
    static int fourPersonTeamAmount  = 0;
    static boolean leftNeighbour = false;

    public static void main(String[] args) throws Exception{
        File file = new File("b_little_bit_of_everything.in");

        RandomAccessFile fileReader = new RandomAccessFile(file, "r");

        String[] lineVars = fileReader.readLine().split(" ");

        pizzaAmount           = getLineVar(lineVars, 0);
        twoPersonTeamAmount   = getLineVar(lineVars, 1);
        threePersonTeamAmount = getLineVar(lineVars, 2);
        fourPersonTeamAmount  = getLineVar(lineVars, 3);

        ArrayList<String[]> pizzas = new ArrayList<>();
        getPizzasFromFile(fileReader, pizzas);

        LinkedList<String[]> sorted = new LinkedList<>(pizzas);
        sorted.sort(Comparator.comparingInt(o -> Integer.parseInt(o[0])));

        LinkedList<String[]> sortedWithoutNums =  removeNums(sorted);

        LinkedList<String> ingredients = createFullIngridientsList(sortedWithoutNums);

        LinkedList<LinkedList<String[]>> pizzasSortedByIngrAmount = sortByAmountOfIngr(sorted, ingredients.size());

        List<int[]> answers = bindAnswers(orderPizzas(
                pizzasSortedByIngrAmount,
                ingredients),
                pizzas);

        System.out.println(answers.size());
        for (int[] arr:answers
             ) {
            for (int i:arr
                 ) {
                System.out.print(i + " ");
            }
            System.out.print("\n");
        }
    }

    static List<List<String[]>> orderPizzas(
                            LinkedList<LinkedList<String[]>> pizzasSortedByIngrAmount,
                            LinkedList<String> ingredients){

        int currentIngrAmount = 0;
        int pendingIngrAmount = 10;

        int bottomBorder = 0;
        int upperBorder = 9;

        List<List<String[]>> orderedPizzas = new LinkedList<>();
        for (int i = 0; i < twoPersonTeamAmount; i++) {
            orderedPizzas.add(new LinkedList<>());
        }
        int orderId = 0;

        String[] firstPizza = new String[ingredients.size()];
        String[] secondPizza = new String[ingredients.size()];
        String[] thirdPizza = new String[ingredients.size()];
        String[] fourthPizza = new String[ingredients.size()];

        boolean firstPizzaDone = false;
        boolean secondPizzaDone = false;
        boolean thirdPizzaDone = false;
        boolean fourthpizzaDone = false;

        //two person team orders processing
        while (twoPersonTeamAmount > 0){

            if(pizzasSortedByIngrAmount.get(upperBorder).size() > 0 & !firstPizzaDone){
                firstPizza = pizzasSortedByIngrAmount.get(upperBorder).getFirst();
                orderedPizzas.get(orderId).add(pizzasSortedByIngrAmount.get(upperBorder).removeFirst());
                firstPizzaDone = true;
            }

            if(pizzasSortedByIngrAmount.get(bottomBorder).size() > 0 & !secondPizzaDone){
                int chosenId = checkForDuplicates(pizzasSortedByIngrAmount, ingredients, bottomBorder, firstPizza);

                orderedPizzas.get(orderId).add(pizzasSortedByIngrAmount.get(bottomBorder).remove(chosenId));
                secondPizzaDone = true;
            }
            if(pizzasSortedByIngrAmount.get(upperBorder).size() == 0 & firstPizzaDone & secondPizzaDone){
                upperBorder--;
            }
            if(pizzasSortedByIngrAmount.get(bottomBorder).size() == 0 & firstPizzaDone & secondPizzaDone){
                bottomBorder++;
            }
            if(firstPizzaDone & secondPizzaDone){
                twoPersonTeamAmount--;
                firstPizzaDone = false;
                secondPizzaDone = false;
                orderId++;
            }
        }

        //three person team orders processing
//        while (threePersonTeamAmount <0){
//            if(pizzasSortedByIngrAmount.get(upperBorder).size() > 0 & !firstPizzaDone){
//                orderedPizzas.get(orderId).add(pizzasSortedByIngrAmount.get(upperBorder).removeFirst());
//                currentIngrAmount = ++upperBorder;
//                pendingIngrAmount -= currentIngrAmount;
//                firstPizzaDone = true;
//            }
//            if(pizzasSortedByIngrAmount.get(bottomBorder).size() > 0 & !secondPizza){
//
//                //check if
//                if(pizzasSortedByIngrAmount.get(pendingIngrAmount).size() > 0){
//
//                }
//
//                orderedPizzas.get(orderId).add(pizzasSortedByIngrAmount.get(bottomBorder).removeFirst());
//                secondPizza = true;
//            }
//        }


        return orderedPizzas;
    }

    static List<int[]> bindAnswers(List<List<String[]>> orderedPizzas, ArrayList<String[]> pizzas){
        List<int[]> answers = new ArrayList<>();

        for (List<String[]> order:orderedPizzas
             ) {
             int[] orderArr = {2, pizzas.indexOf(order.get(0)), pizzas.indexOf(order.get(1))};
             answers.add(orderArr);
        }
        return answers;
    }

    static int checkForDuplicates(LinkedList<LinkedList<String[]>> pizzasSortedByIngrAmount,
                                   LinkedList<String> ingredients, int bottomBorder, String[] firstPizza){

        List<Integer[]> candidates = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        List<Integer> duplicates = new ArrayList<>();

        //для каждой пиццы из конкретного кластера
        for (String[] pizza:pizzasSortedByIngrAmount.get(bottomBorder)
        ) {
            int duplicateAmount = 0;
            //и для каждого ингридиента этой пиццы
            for (String ingr:pizza
            ) {
                int ingrID = ingredients.indexOf(ingr);
                //будем сравнивать его с ингридиентами первой пиццы
                for (String firstPizzaIngr:firstPizza
                ) {
                    //если ингридиент 1 пиццы равен ингридиенту 2 пиццы
                    if(ingrID == ingredients.indexOf(firstPizzaIngr)){
                        duplicateAmount++;
                        int id = pizzasSortedByIngrAmount.get(bottomBorder).indexOf(pizza);
                        ids.add(id);
                        duplicates.add(duplicateAmount);
                        candidates.add(new Integer[]{id, duplicateAmount});
                    }
                }
            }
        }
        int chosenPizzaId = 0;
        duplicates.sort(Integer::compareTo);
        for (Integer[] arr:candidates
             ) {
            int id = arr[0];
            int dupl = arr[1];
            if(dupl == duplicates.get(0))
            {
                chosenPizzaId = id;
            }

        }
        return chosenPizzaId;
    }

    static LinkedList<LinkedList<String[]>> sortByAmountOfIngr(LinkedList<String[]> input, int totalIngrAmount){
        LinkedList<LinkedList<String[]>> result = new LinkedList<>();
        for (int i = 0; i < totalIngrAmount; i++) {
            result.add(i, new LinkedList<String[]>());
        }

        for (String[] arr:input
             ) {
            int index = Integer.parseInt(arr[0]);
            result.get(index-1).add(arr);
        }
        return result;
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
        LinkedList<String> ingredients = new LinkedList<>(Arrays.asList(sortedPizzasCopy.getLast()));

        while (sortedPizzasCopy.size() > 1) {
            sortedPizzasCopy.removeLast();
            //lesserDiff is what is present in lesser, but absent in current ingridients list
            LinkedList<String> lesserDiff = new LinkedList<>(Arrays.asList(sortedPizzasCopy.getLast()));
            lesserDiff.removeAll(ingredients);

            if (lesserDiff.size() > 0) {
                LinkedList<String> lesser = new LinkedList<>(Arrays.asList(sortedPizzasCopy.removeLast()));

                for (String ingr : lesserDiff
                ) {
                    int ingrID = lesser.indexOf(ingr);  //id of ingr in lesser that is absent in bigger

                    //get ingr neighbours in lesser
                    String neighbour = findLesserNeighbours(ingrID, lesser, 1);

                    if (neighbour == null) {
                        return ingredients;
                    }

                    int ingrIdInIngridients = 0;
                    if (Main.leftNeighbour) {
                        ingrIdInIngridients = ingredients.indexOf(neighbour) + 1;
                    } else if ((ingredients.indexOf(neighbour) - 1) > -1) {
                        ingrIdInIngridients = ingredients.indexOf(neighbour) - 1;
                    }
                    ingredients.add(ingrIdInIngridients, ingr);
                }
            }
        }
        return ingredients;
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
