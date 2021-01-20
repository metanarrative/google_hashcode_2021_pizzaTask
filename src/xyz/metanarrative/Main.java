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
    static int upperBorder = 9;
    static int pendingIngrAmount = 0;

    public static void main(String[] args) throws Exception{
        File file = new File("b_little_bit_of_everything.in");

        RandomAccessFile fileReader = new RandomAccessFile(file, "r");

        String[] lineVars = fileReader.readLine().split(" ");

        pizzaAmount           = getLineVar(lineVars, 0);
        twoPersonTeamAmount   = getLineVar(lineVars, 1);
        threePersonTeamAmount = getLineVar(lineVars, 2);
        fourPersonTeamAmount  = getLineVar(lineVars, 3);

        LinkedList<LinkedList<String>> pizzas = new LinkedList<>();
        getPizzasFromFile(fileReader, pizzas);

        LinkedList<LinkedList<String>> sorted = new LinkedList<LinkedList<String>>(pizzas);
        sorted.sort(Comparator.comparingInt(o -> Integer.parseInt(o.get(0))));

        LinkedList<String> ingredients = createFullIngridientsList(sorted);

        ingredients = removeNumsAtIngredients(ingredients);
        pendingIngrAmount = ingredients.size();

        LinkedList<LinkedList<LinkedList<String>>> pizzasSortedByIngrAmount = sortByAmountOfIngr(sorted, ingredients.size());

        removeNums(pizzasSortedByIngrAmount);

        List<int[]> answers = bindAnswers(orderPizzas(pizzasSortedByIngrAmount, ingredients), pizzas);

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

    static List<List<LinkedList<String>>> orderPizzas(
                            LinkedList<LinkedList<LinkedList<String>>> pizzasSortedByIngrAmount,
                            LinkedList<String> ingredients){

        //List of lists for each teamnumber that contain pizzas (List<String> is a pizza with ingredients)
        List<List<LinkedList<String>>> orderedPizzas = new LinkedList<>();
        for (int i = 0; i < twoPersonTeamAmount; i++) {
            orderedPizzas.add(new LinkedList<>());
        }
        int orderId = 0;

        LinkedList<String> firstPizza = new LinkedList<>();
        LinkedList<String> secondPizza = new LinkedList<>();
        LinkedList<String> thirdPizza = new LinkedList<>();
        LinkedList<String> fourthPizza = new LinkedList<>();

        boolean firstPizzaDone = false;
        boolean secondPizzaDone = false;
        boolean thirdPizzaDone = false;
        boolean fourthpizzaDone = false;

        //two person team orders processing
        while (twoPersonTeamAmount > 0){

            if(!firstPizzaDone){
                LinkedList<LinkedList<String>> cluster = pizzasSortedByIngrAmount.get(upperBorder); //each cluster has pizzas with same ingr amount
                cluster = checkCluster(pizzasSortedByIngrAmount, cluster, false);
                firstPizza = cluster.removeFirst();
                orderedPizzas.get(orderId).add(firstPizza);
                firstPizzaDone = true;
                pendingIngrAmount = pendingIngrAmount - (upperBorder+1);
            }
            //if all ingrs are present in prev pizza, but you need more:
            if(pendingIngrAmount-1 <= 0){
                pendingIngrAmount= 1;
            }

            if(!secondPizzaDone){
                LinkedList<LinkedList<String>> cluster = pizzasSortedByIngrAmount.get(pendingIngrAmount-1);
                cluster = checkCluster(pizzasSortedByIngrAmount, cluster, true);
                int chosenId = checkForDuplicates(pizzasSortedByIngrAmount, ingredients, firstPizza);
                secondPizza = cluster.remove(chosenId);
                orderedPizzas.get(orderId).add(secondPizza);
                secondPizzaDone = true;
            }

            if(firstPizzaDone & secondPizzaDone){
                twoPersonTeamAmount--;
                firstPizzaDone = false;
                secondPizzaDone = false;
                orderId++;
                pendingIngrAmount = ingredients.size();
            }
        }
        return orderedPizzas;
    }

    //boolean direction means movement up or down in cluster list. true is up, false is down.
    static LinkedList<LinkedList<String>> checkCluster(LinkedList<LinkedList<LinkedList<String>>> pizzasSortedByIngrAmount,
                                       LinkedList<LinkedList<String>> cluster,
                                       boolean direction)
    {
        //if cluster is empty - move to a cluster below
        if(cluster.size() < 1 && !direction){
            Main.upperBorder--;
            cluster = pizzasSortedByIngrAmount.get(Main.upperBorder);
        }
        if(cluster.size() < 1 && direction){
            pendingIngrAmount++;
            cluster = pizzasSortedByIngrAmount.get(pendingIngrAmount-1);
        }

        return cluster;
    }

    static List<int[]> bindAnswers(List<List<LinkedList<String>>> orderedPizzas, LinkedList<LinkedList<String>> pizzas){
        List<int[]> answers = new ArrayList<>();

        for (List<LinkedList<String>> order:orderedPizzas
             ) {
            LinkedList<String> pizzaFromOrder1 = order.get(0);
            int firstIndex = getPizzaIndex(pizzaFromOrder1, pizzas);
            LinkedList<String> pizzaFromOrder2 = order.get(1);
            int secondIndex = getPizzaIndex(pizzaFromOrder2, pizzas);

            int[] orderArr = {2, firstIndex, secondIndex};
            answers.add(orderArr);
        }
        return answers;
    }

    static int getPizzaIndex(LinkedList<String> pizza, LinkedList<LinkedList<String>> pizzaList){
        int index = 0;
        for (int i = 0; i < pizzaList.size(); i++) {
            LinkedList<String> pizzaFromStorage = pizzaList.get(i);
            if(pizza == pizzaFromStorage){
                index = i;
            }
        }
        return index;
    }

    static int checkForDuplicates(LinkedList<LinkedList<LinkedList<String>>> pizzasSortedByIngrAmount,
                                   LinkedList<String> ingredients,
                                   List<String> firstPizza){

        List<Integer[]> candidates = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        List<Integer> duplicates = new ArrayList<>();

        //для каждой пиццы из конкретного кластера
        for (List<String> pizza:pizzasSortedByIngrAmount.get(pendingIngrAmount-1)
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
                        int id = pizzasSortedByIngrAmount.get(pendingIngrAmount-1).indexOf(pizza);
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

    static LinkedList<LinkedList<LinkedList<String>>> sortByAmountOfIngr(LinkedList<LinkedList<String>> input, int totalIngrAmount){
        LinkedList<LinkedList<LinkedList<String>>> result = new LinkedList<>();
        for (int i = 0; i < totalIngrAmount; i++) {
            result.add(i, new LinkedList<LinkedList<String>>());
        }

        for (LinkedList<String> arr:input
             ) {
            int index = Integer.parseInt(arr.get(0));
            result.get(index-1).add(arr);
        }
        return result;
    }

    static void removeNums(LinkedList<LinkedList<LinkedList<String>>> pizzasSortedByIngrAmount){
        for (LinkedList<LinkedList<String>> cluster:pizzasSortedByIngrAmount
             ) {
            for (List<String> pizza:cluster
            ) {
                pizza.remove(0);
            }
        }
    }

    static LinkedList<String> removeNumsAtIngredients(LinkedList<String> ingredients){
        ArrayList<Integer> ids = new ArrayList<>();

        for (int i = 0; i < ingredients.size(); i++) {
            try{
                int a = Integer.parseInt(ingredients.get(i));
                ids.add(i);
            }
            catch (NumberFormatException e){

            }
        }
        for (Integer i:ids
        ) {
            ingredients.set(i, null);
        }

        LinkedList<String> newIngrs = new LinkedList<>();

        for (String s:ingredients
             ) {
            if(s != null){
                newIngrs.add(s);
            }
        }

        return newIngrs;
    }

    static LinkedList<String> createFullIngridientsList(LinkedList<LinkedList<String>> sortedPizzas){
        LinkedList<LinkedList<String>> sortedPizzasCopy = new LinkedList<>(sortedPizzas);
        LinkedList<String> ingredients = new LinkedList<>((sortedPizzasCopy.getLast()));

        while (sortedPizzasCopy.size() > 1) {
            sortedPizzasCopy.removeLast();
            //lesserDiff is what is present in lesser, but absent in current ingridients list
            LinkedList<String> lesserDiff = new LinkedList<>(sortedPizzasCopy.getLast());
            lesserDiff.removeAll(ingredients);

            if (lesserDiff.size() > 0) {
                LinkedList<String> lesser = new LinkedList<>(sortedPizzasCopy.removeLast());

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

    static void getPizzasFromFile(RandomAccessFile fileReader, LinkedList<LinkedList<String>> pizzas){
        try {
            while (true){
                LinkedList<String> pizzaLine = new LinkedList<>(Arrays.asList(fileReader.readLine().split(" ")));
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
