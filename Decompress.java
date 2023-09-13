/* This program decompresses a given compressed file
 * 
 * Names:
 * Kieran Kennedy
 * Mathisha Karunaratne
 * 
 * Date
 * 5/09/2023
 */

import java.io.*;
import java.util.*;
import java.time.*;
import java.text.DecimalFormat;

public class Decompress {
    public static void main(String[] args) {
        Scanner stdin = new Scanner(System.in);
        String filename;
        String user_response = "";

        // Prompt for filename if needed
        if (args.length > 0) {
            filename = args[0];
        } else {
            System.out.println("What is the name of the file to decompress?");
            filename = stdin.nextLine();
        }
        do {   
            try {
                File file = new File(filename);
                boolean valid = false;
                ObjectInputStream input;
                
                // Check for valid filename
                do {
                    try {
                        if (filename.contains(".zzz")) {
                            input = new ObjectInputStream(new FileInputStream(file));
                            valid = true;
                        } else {
                            System.out.println("What is the name of the file to compress?");
                    
                            filename = stdin.nextLine();
                            file = new File(filename);
                        }
                    } catch (FileNotFoundException e) {
                        System.out.println("What is the name of the file to compress?");
                    
                        filename = stdin.nextLine();
                        file = new File(filename);
                    }
                } while (!valid);

                input = new ObjectInputStream(new FileInputStream(file));
                List<String> table = new ArrayList<String>();

                long start_time = System.nanoTime();

                // Initialize table with ASCII characters
                for (int i = 0; i < 128; i++) {
                    table.add(String.valueOf((char) i));
                }

                PrintWriter output = new PrintWriter(new FileOutputStream(filename.substring(0, filename.length() - 4)));

                try {
                    String previous_value = table.get(input.readInt());
                    output.print(previous_value);
                    int current_index;
                    
                    while (true) {
                        current_index = input.readInt();
                        int size = table.size();
                        
                        // Check to see if the current index is within the table and has a value associated with it
                        if ((size > current_index) && (table.get(current_index) != null)) {
                            String current_value = table.get(current_index);
                            output.print(current_value);
                            table.add(previous_value + current_value.substring(0, 1));
                            previous_value = current_value;

                        } else {
                            output.print(previous_value + previous_value.substring(0, 1));
                            
                            if (size > current_index) {
                                table.set(current_index, previous_value + previous_value.substring(0, 1));
                            } else {
                                // Increase the table size to store the current value
                                while (size < current_index + 1) {
                                    table.add(null);
                                    size = table.size();
                                }
                                table.set(current_index, previous_value + previous_value.substring(0, 1));
                            }

                            previous_value = table.get(current_index);
                        }
                    }
                } catch (EOFException e) {
                    long end_time = System.nanoTime();
                    
                    try {
                        input.close();
                        output.close();

                        DecimalFormat df = new DecimalFormat("###.###");
                        PrintWriter log_file = new PrintWriter(new FileOutputStream(filename.substring(0, filename.length() - 4) + ".log"));
                        
                        // Write to the log file
                        log_file.println("Deompression for file " + filename);
                        log_file.println("Compression took " + df.format(((end_time - start_time) / Math.pow(10, 9))) + " seconds");
                        log_file.println("The table was doubled 0 times");

                        log_file.close();

                        // Prompt the user to run the program again
                        System.out.println("Would you like to run again? (y/n)");
                        user_response = stdin.nextLine();

                        filename = "";
                        
                    } catch (IOException error) {
                        System.out.println(e);
                    }
                    
                }

            } catch (IOException e) {
                System.out.println(e);
            }
        } while(user_response.equalsIgnoreCase("y"));
    }
}
