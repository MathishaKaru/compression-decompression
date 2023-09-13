/* This program takes in a file and compresses it into a binary file using a compression algorithm
 * 
 * Names:
 * Kieran Kennedy
 * Mathisha Karunaratne
 * 
 * Date
 * 5/09/2023
 */

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.time.*;

public class Compress {
    public static void main(String[] args) {
        Scanner stdin = new Scanner(System.in);
        String filename;
        String user_response = "";
        
        // Prompt user for filename if needed
        if (args.length > 0) {
            filename = args[0];
        } else {
            System.out.println("What is the name of the file to compress?");
            filename = stdin.nextLine();
        }
        
        do {
            try {
                File file = new File(filename);
                boolean valid = false;
                BufferedReader input;
                
                // Check for valid filename
                do {
                    try {
                        input = new BufferedReader(new FileReader(file));
                        valid = true;
                    } catch (FileNotFoundException e) {
                        System.out.println("What is the name of the file to compress?");
                    
                        filename = stdin.nextLine();
                        file = new File(filename);
                    }
                } while (!valid);

                input = new BufferedReader(new FileReader(file));
                ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(filename + ".zzz"));

                // Choose a prime table size
                HashTableChain prime_check = new HashTableChain<String, Integer>(0);

                int table_size = (int) (file.length() / 1000) * (int) Math.log(file.length());

                if (table_size % 2 == 0) {
                    table_size++;
                }

                boolean prime = prime_check.isPrime(table_size);
                
                while (!prime) {
                    table_size += 2;
                    prime = prime_check.isPrime(table_size);
                }

                long start_time = System.nanoTime();
                
                HashTableChain table = new HashTableChain<String, Integer>(table_size);
                                
                // Initialize the table with all ASCII characters
                for (int i = 0; i < 128; i++) {
                    table.put(String.valueOf((char) i), i);
                }

                int file_char;
                int index = 128;
                String prefix = "";
                String to_write = "";

                // Compress the file by writing to a seperate chaining hash table
                while((file_char = input.read()) != -1) {
                    prefix += String.valueOf((char) file_char);

                    while (table.get(prefix) != null) {
                        prefix += String.valueOf((char) input.read());
                    }

                    to_write = prefix.substring(0, prefix.length() - 1);
                    
                    output.writeInt((int) table.get(to_write));
                    table.put(prefix, index);
                    index++;
                    
                    prefix = prefix.substring(prefix.length() - 1);
                    output.flush();
                }
                
                long end_time = System.nanoTime();
                                
                PrintWriter log_file = new PrintWriter(new FileOutputStream(filename + ".zzz.log"));

                input.close();
                output.close();
                
                File output_file = new File(filename + ".zzz");

                DecimalFormat df = new DecimalFormat("###.###");

                // Outputs information into log file
                log_file.println("Compression of " + filename);

                if (file.length() < 1000) {
                    log_file.println("Compressed from " + file.length() + " Bytes to " + output_file.length() + " Bytes");
                } else {
                    log_file.println("Compressed from " + file.length() / 1000 + " Kilobytes to " + output_file.length() / 1000 + " Kilobytes");
                }

                log_file.println("Compression took " + df.format(((end_time - start_time) / Math.pow(10, 9))) + " seconds");
                log_file.println("The dictionary contains " + table.size() + " total entries");
                log_file.println("The table was rehashed " + table.rehashes() + " times");
            
                log_file.close();

                // Reprompt user to run program again
                System.out.println("Would you like to run again? (y/n)");
                user_response = stdin.nextLine();

                filename = "";
                
            } catch (IOException e) {
                System.out.println(e);
            }
        } while (user_response.equalsIgnoreCase("y"));
    }
}
