import java.io.*;
import java.util.*;

public class frontEnd {
    public static void main(String[] args) {
        File inputFile = new File("input.txt");
        ArrayList<String> nodes = new ArrayList<>();
        ArrayList<String> treasures = new ArrayList<>();
        int numSteps = 0;
        HashMap<String, ArrayList<String>> nodeNeighbors = new HashMap<>();
        HashMap<String, ArrayList<String>> nodeTreasures = new HashMap<>();

        try {
            Scanner scanner = new Scanner(inputFile);

            // Read nodes
            String nodesLine = scanner.nextLine();
            String[] nodesArray = nodesLine.split(" ");
            for (String node : nodesArray) {
                nodes.add(node);
            }

            // Read treasures
            String treasuresLine = scanner.nextLine();
            String[] treasuresArray = treasuresLine.split(" ");
            for (String treasure : treasuresArray) {
                treasures.add(treasure);
            }

            // Read allowed steps
            numSteps = scanner.nextInt();
            scanner.nextLine(); // Consume the remaining newline character

            // Read maze structure
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(" ");
                String node = line[0];
                int i = 1;
                if (line[i].equals("TREASURES")) {
                    i++;
                    ArrayList<String> currentTreasures = new ArrayList<>();
                    while (!line[i].equals("NEXT")) {
                        //System.out.println(line[i]);
                        currentTreasures.add(line[i]);
                        i++;
                    }
                    nodeTreasures.put(node, currentTreasures);
                } if (line[i].equals("NEXT")) {
                    i++;
                    ArrayList<String> currentConnections = new ArrayList<>();
                    for (int j = i; j < line.length; j++) {
                        currentConnections.add(line[j]);
                    }
                    nodeNeighbors.put(node, currentConnections);
                }
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }

        // generate set of clauses
        List<String> clauses = new ArrayList<>();
        int numNodes = nodes.size();

        //generate atoms and corresponding int values
        HashMap<String, Integer> Atoms = new HashMap<>();
        int count = 1;
        // loop over all nodes, treasures, and steps and add them to the map
        for (int i = 0; i <= numSteps; i++) {
            for (int j = 0; j < treasures.size(); j++) {
                String atom = "Has(" + treasures.get(j) + "," + i + ")";
                //System.out.println(atom);
                Atoms.put(atom, count);
                count++;
            }
            for (int j = 0; j < numNodes; j++) {
                String atom = "At(" + nodes.get(j) + "," + i + ")";
                //System.out.println(atom);
                Atoms.put(atom, count);
                count++;
            }
        }
        // System.out.println(Atoms);
        // System.out.println("=====================================");

        //assign each node as their index number
        // 1. Player at only one place at a time
        //At(N,I) means that the player is on node N at time I.
        for (int i = 0; i <= numSteps; i++) {
            for (int j = 0; j < numNodes; j++) {
                for (int k = j + 1; k < numNodes; k++) {
                    //At(j,i) -> -At(k,i)
                    //"-At("+j+","+i+")"+" V "+"-At("+k+","+i+")"
                    String clause1 = "At("+nodes.get(j)+","+i+")";
                    String clause2 = "At("+nodes.get(k)+","+i+")";
                    clauses.add(-1*Atoms.get(clause1)+" "+-1*Atoms.get(clause2));
                }
            }
        }

        // 2. Player moves on edges
        //System.out.println("2. Player moves on edges");
        for (int i = 0; i < numSteps; i++) {
            for (String node : nodeNeighbors.keySet()) {
                ArrayList<String> neighbors = nodeNeighbors.get(node);

                String clause1 = "At("+node+","+i+")";
                String clauseAll = -1*Atoms.get(clause1)+"";
                //String clauseAllText = "-At("+node+","+i+")";

                for (String otherNode : neighbors) {
                    //¬At(N,I) ∨ At(M1,I+1) ∨... ∨ At(Mk,I+1).
                        String clause2 = "At("+otherNode+","+(i+1)+")";
                        clauseAll += " "+Atoms.get(clause2);
                        clauseAllText += " V "+clause2;
                        // //"-At("+nodes.indexOf(node)+","+i+")"+" V "+"-At("+nodes.indexOf(otherNode)+","+i+1+")"
                        // String clause1 = "At("+node+","+i+")";
                        // String clause2 = "At("+otherNode+","+(i+1)+")";
                        // System.out.println(clause1+" V "+clause2);
                        // clauses.add(-1*Atoms.get(clause1)+" "+-1*Atoms.get(clause2));
                }
                if (neighbors.size()>0){
                    //System.out.println(clauseAllText);
                    clauses.add(clauseAll);
                }
            }
        }

        // 3. Player gets treasures at nodes
        //System.out.println("3. Player gets treasures at nodes");
        //for every time I
        for (int i = 0; i <= numSteps; i++) {
            //for every node N
            for (int j = 0; j < numNodes; j++) {
                String node = nodes.get(j);
                //for every treasure T
                for (String treasure : nodeTreasures.get(node)) {
                    //¬At(N,I) ∨ Has(T,I)
                    //"-At("+node+","+i+")"+" V "+"Has("+treasure+","+i+")"
                    String clause1 = "At("+node+","+i+")";
                    String clause2 = "Has("+treasure+","+i+")";
                    //System.out.println(clause1+" V "+clause2);
                    clauses.add(-1*Atoms.get(clause1)+" "+Atoms.get(clause2));
                }
            }
        }

        //4. If the player has treasure T at time I-1, then the player has T at time I. (I=1..K)
        //System.out.println("If the player has treasure T at time I-1, then the player has T at time I. (I=1..K)");
        //for every time I
        for (int i = 1; i <= numSteps; i++) {
            //for every treasure T
            for (String treasure : treasures) {
                //¬Has(T,I-1) ∨ Has(T,I)
                //"-Has("+treasure+","+(i-1)+")"+" V "+"Has("+treasure+","+i+")"
                String clause1 = "Has("+treasure+","+(i-1)+")";
                String clause2 = "Has("+treasure+","+i+")";
                //System.out.println("-"+clause1+" V "+clause2);
                
                clauses.add(-1*Atoms.get(clause1)+" "+Atoms.get(clause2));
            }
        }

        //5. If the player does not have treasure T at time I-1 and has T at time I, 
        //then at time I they must be at one of the nodes M1 ... Mq.
        //HashMap<String, ArrayList<String>> treasureToNodes = new HashMap<>();
        //for every step
        for (int i = 1; i<=numSteps;i++){
            //for every type of treasure
            for (String treasure : treasures) {
                //In CNF Has(T,I-1) ∨ ¬Has(T,I) ∨ At(M1,I) ∨ At(M2,I) ∨ ... ∨At(Mq,I).
                String clause1 = "Has("+treasure+","+(i-1)+")";
                String clause2 = "Has("+treasure+","+i+")";
                String clauseAll = Atoms.get(clause1)+" "+(-1*Atoms.get(clause2));
                String clauseAllText = clause1+" V -"+clause2;
                for (Map.Entry<String, ArrayList<String>> entry : nodeTreasures.entrySet()) {
                    //all the treasures that this node contains
                    String currentNode = entry.getKey();
                    ArrayList<String> currentTreasures = entry.getValue();
                    if (currentTreasures.contains(treasure)){
                        //∨ At(M1,I) ∨ At(M2,I) ∨ ... ∨At(Mq,I).
                        clauseAllText+=" V At("+currentNode+","+i+")";
                        clauseAll += " "+Atoms.get("At("+currentNode+","+i+")");
                    }
                }
                //System.out.println(clauseAllText);
                clauses.add(clauseAll);
            }
        }

        //6. The player is at START at time 0. At(START,0).
        clauses.add(Atoms.get("At(START,0)")+"");

        //7. At time 0, the player has none of the treasures.
        //8. At time K, the player has all the treasures.
        for (String treasure : treasures) {
            //In CNF Has(T,I-1) ∨ ¬Has(T,I) ∨ At(M1,I) ∨ At(M2,I) ∨ ... ∨At(Mq,I).
            String clause1 = "Has("+treasure+",0)";
            String clause2 = "Has("+treasure+","+numSteps+")";
            clauses.add(-1*Atoms.get(clause1)+"");
            clauses.add(Atoms.get(clause2)+"");
        }


        File fileOut = new File("clauses.txt");
        try {
            PrintWriter writer = new PrintWriter(fileOut);
            for (String clause : clauses) {
                writer.println(clause);
            }
            writer.println(0);
            for (int iterator = 1; iterator < Atoms.size()+1; iterator++){
                for (Map.Entry<String, Integer> entry : Atoms.entrySet()) {
                    if (entry.getValue()==iterator){
                        writer.println(entry.getValue() + " " + entry.getKey());
                    }
                }
            }
            writer.close();
            System.out.println("Clauses written to file.");
        } catch (FileNotFoundException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
}
