import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class DavisPutnam {
    public static void main(String[] args) {
        //add clauses
        File file = new File("clauses.txt");
        ArrayList<ArrayList<Integer>> clauses = new ArrayList<>();
        String restOfFile = "0";
        Scanner scanner;
        try {
            scanner = new Scanner(file);
    
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equals("0")){
                    break;
                }
                String[] tokens = line.split(" ");
                ArrayList<Integer> clause = new ArrayList<>();
                for (String token : tokens) {
                    clause.add(Integer.parseInt(token));
                }
                clauses.add(clause);
            }
            while (scanner.hasNextLine()) {
                restOfFile += "\n" + scanner.nextLine();
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Set<Integer> atoms= new HashSet<Integer>();
        for(ArrayList<Integer> clause: clauses){
            for(int literal: clause){
                    atoms.add(Math.abs(literal));
            }
        }

        ArrayList <Integer> bindings = new ArrayList<Integer>();

        ArrayList <Integer> result = dpll(clauses, bindings, atoms);
        if (result == null) {
            System.out.println("Fail");
        } else {
            System.out.println("Bindings: " + result);
        }

        File fileOut = new File("results.txt");
        try {
            PrintWriter writer = new PrintWriter(fileOut);
            for (int binding : result) {
                writer.println(Math.abs(binding)+" "+(binding>0 ? "T" : "F"));
            }
            writer.println(restOfFile);
            writer.close();
            System.out.println("Clauses written to file.");
        } catch (FileNotFoundException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    public static ArrayList <Integer> dpll(ArrayList<ArrayList<Integer>> clauses, ArrayList <Integer> bindings, Set<Integer> atoms) {
        while (true) {
            if (bindings.size()>10){
                return bindings;
            }
            System.out.println(bindings);
            if (clauses.isEmpty()) {
                return bindings;
            }
            if (containsEmptyClause(clauses)) {
                return null;
            }
            int singletonAtom = hasSingletonClause(clauses);
            int pureLiteral = hasPureLiteral(clauses);
            if (singletonAtom != -1) {
                
                System.out.println("singletonAtom");
                propagate(clauses, bindings, singletonAtom);
            }
            else if (pureLiteral != -1){
                System.out.println("pureLiteral");
                propagate(clauses, bindings, pureLiteral);
            }
            else {
                System.out.println("else");
                ArrayList<ArrayList<Integer>> newClauses = copyClauses(clauses);
                ArrayList <Integer> newBindings = new ArrayList <Integer>(bindings);

                int literal = chooseUnboundAtom(bindings,atoms);
                propagate(newClauses, newBindings, literal);

                ArrayList <Integer> answer = dpll(newClauses, newBindings,atoms);
                if (answer != null) {
                    return answer;
                }
                
                System.out.println("changing from "+literal+" to "+Integer.valueOf(-1*literal));
                propagate(clauses, bindings, Integer.valueOf(-1*literal));
            }
        }
    }
    
    private static boolean containsEmptyClause(ArrayList<ArrayList<Integer>> clauses) {
        for (ArrayList<Integer> clause: clauses){
            if (clause.isEmpty()){
                return true;
            }
        }
        return false;
    }

    private static int hasSingletonClause(ArrayList<ArrayList<Integer>> clauses) {
        for (ArrayList<Integer> clause : clauses) {
            if (clause.size() == 1) {
                return clause.get(0);
            }
        }
        return -1;
    }

    private static int hasPureLiteral(ArrayList<ArrayList<Integer>> clauses) {
        HashSet<Integer> positiveAtoms = new HashSet<>();
        HashSet<Integer> negativeAtoms = new HashSet<>();
        for (ArrayList<Integer> clause : clauses) {
            for (Integer literal : clause) {
                if (literal > 0) {
                    positiveAtoms.add(literal);
                } else {
                    negativeAtoms.add(-literal);
                }
            }
        }
    
        for (Integer atom : positiveAtoms) {
            if (!negativeAtoms.contains(atom)) {
                return atom;
            }
        }
    
        for (Integer atom : negativeAtoms) {
            if (!positiveAtoms.contains(atom)) {
                return atom;
            }
        }
    
        return -1;
    }

    private static ArrayList<ArrayList<Integer>> copyClauses(ArrayList<ArrayList<Integer>> clauses) {
        ArrayList<ArrayList<Integer>> copiedClauses = new ArrayList<>();
        for (ArrayList<Integer> clause : clauses) {
            ArrayList<Integer> copiedClause = new ArrayList<>(clause);
            copiedClauses.add(copiedClause);
        }
        return copiedClauses;
    }
    
    private static int chooseUnboundAtom(ArrayList <Integer> bindings, Set<Integer> atoms) {
        for (int atom : atoms) {
            if (!bindings.contains(atom) && !bindings.contains(Integer.valueOf(-1*atom))) {
                return atom;
            }
        }
        return -1;
    }

    private static void propagate(ArrayList<ArrayList<Integer>> clauses, ArrayList <Integer> bindings, int p) {
        
        // System.out.println("clauses: \n"+clauses);
        // System.out.println("bindings: \n"+bindings);
        // Set the value of the given atom p in the bindings map
        bindings.add(p);
    
        // remove from clauses
        ArrayList<ArrayList<Integer>> newClauses = new ArrayList<>();
        for (ArrayList<Integer> clause: clauses){
            ArrayList<Integer> newClause = new ArrayList<>(clause);
            if (clause.contains(p)){
                //remove
            }
            else if (clause.contains(-1*p)){
                System.out.println("removed "+p+" from "+clause);
                newClause.remove(Integer.valueOf(-1*p));
                newClauses.add(newClause);
            }
            else{
                newClauses.add(newClause);
            }
        }
        clauses.clear();
        clauses.addAll(newClauses);
    }
}
