import java.io.*;
import java.util.*;

public class backEnd {
    public static void main(String[] args) {
           // Read output values into array of booleans
           Scanner outputScanner;
           HashMap<String, Boolean> outputValues = new HashMap<String, Boolean>();
           HashMap<String, String> atomValues = new HashMap<String, String>();
        try {
            outputScanner = new Scanner(new File("results.txt"));
            while (outputScanner.hasNext()) {
                String line = outputScanner.nextLine().trim();
                String[] parts = line.split(" ");
                if (line.equals("0")){
                    break;
                }
                if (parts[1].equals("T")) {
                    outputValues.put(parts[0],true);
                } else if (parts[1].equals("F")) {
                    outputValues.put(parts[0],false);
                }
            }
            while (outputScanner.hasNext()) {
                String line = outputScanner.nextLine().trim();
                String[] parts = line.split(" ");
                String key = parts[0].trim();
                String value = parts[1].trim();
                atomValues.put(key, value);
            }
            outputScanner.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
   
   
           // Create array of locations at each time step
           ArrayList<String> map = new ArrayList<String>();
           for (String atom : outputValues.keySet()) {
                if (outputValues.get(atom) == true){
                    String value = atomValues.get(atom);
                    if (value.startsWith("At(")) {
                        map.add(value);
                    }
                }
           }
           String[] result = new String[map.size()];
           for (String atom: map){
                int time = Integer.parseInt(atom.substring(atom.indexOf(",")+1,atom.indexOf(")")));
                result[time] = atom.substring(atom.indexOf("(")+1,atom.indexOf(","));
           }
   
           
           for (int i = 0; i < result.length;i++){
            System.out.printf(result[i]+" ");
           }
       }
}
