import java.net.*;
import java.util.*;
import java.io.*;
import java.util.Scanner;

public class Server {
    public static int line_number = 2;
    public static boolean isStart = true;
    public static final String GREEN = "\u001B[32m";
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String BLUE = "\u001B[34m";

    public static void main(String[] args) {
        // Database operations
        String [] options = {"    0. Search a Friend", "    1. Add a Friend", "    2. Update Friend's Details", "    3. Delete a Friend", "    4. List Friends", "    5. Exit"};
        int choice = -1;
        String details, name, surname, telephone_number;

        ServerSocket sever_socket = null;
        while (true) {
            try {
                sever_socket = new ServerSocket(55550);
                if (isStart) System.out.println("Server has started.");
                if (isStart) System.out.println("Waiting for client....");
                Socket socket = sever_socket.accept();
                if (isStart) System.out.println("Client Accepted!") ;

                // To stop the printing above
                isStart = false;

                PrintWriter clientWriter = new PrintWriter(socket.getOutputStream(), true);                         //  To write to the client
                BufferedReader clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));           //  To read from client
                clientWriter.println("WELCOME TO MUZI AND ISHEANESU'S SERVER");

                while (choice != 5) {
                    clientWriter.println(print("What would you like to do:"));
                    for (String s : options) clientWriter.println(print(s));
                    clientWriter.println(print(""));

                    //  Read choice
                    do {
                        clientWriter.println(print("Choice:"));
                        choice = Integer.parseInt(clientReader.readLine());
                        System.out.println(choice);
                        clientWriter.print(print(""));
                    } while (choice < 0 || choice > 5);

                    clientWriter.println("\033[2J");
                    line_number = 0;
                    clientWriter.println("\033[0;0H");

                    switch (choice) {
                        case 0:
                            clientWriter.print(GREEN + print("[=========================================== SEARCHING FOR FRIEND ===========================================]") + RESET);
                            clientWriter.println(print("Please enter the name and surname / telephone number of the person you are looking for: "));
                            details = clientReader.readLine();
                            File file  = new File("Database.txt");
                            int lineNum = 0;
                            try{
                                Scanner scanner = new Scanner(file);
                                while(scanner.hasNextLine()){
                                    String line = scanner.nextLine();
                                    lineNum++;
                                    boolean Found = false;
                                    String[] words=line.split(", ");  //Split the word using space
                                    for (String word : words) 
                                    {
                                        if (word.equals(details))
                                        {
                                            clientWriter.println(print("[" + BLUE + "SUCCESS" + RESET +"]: Friend has been found."));
                                            clientWriter.println(print(line));
                                            clientWriter.print(print(""));
                                            Found = true;
                                            break;
                                        }  
                                    }
                                    if(Found == true){
                                        break;
                                    }
                                    if(!scanner.hasNextLine()){
                                        clientWriter.println(print("[" + RED + "FAILURE" + RESET +"]: Friend has not been found."));
                                        clientWriter.print(print(""));
                                    }
                                }
                                scanner.close();
                            }catch(FileNotFoundException e){
                            }
                            break;

                        case 1:
                            clientWriter.println(GREEN + print("[=========================================== ADDING A FRIEND ===========================================]") + RESET);
                            clientWriter.println(print("Name of Friend: "));
                            name = clientReader.readLine();
                            System.out.println(name);
                            clientWriter.println(print("Surname of Friend: "));
                            surname = clientReader.readLine();
                            System.out.println(surname);
                            clientWriter.println(print("Telephone Number of Friend: "));
                            telephone_number = clientReader.readLine();
                            System.out.println(telephone_number);

                            if (!name.matches("[a-zA-Z]+") || !surname.matches("[a-zA-Z]+") || telephone_number.matches("[a-zA-Z]+")) {
                                clientWriter.println(print("[" + RED + "FAILED" + RESET + "]: Could not add friend because the name / surname is not alpha or the telephone is not numeric."));
                                clientWriter.println(print(""));
                            }
                            else {
                                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("Database.txt", true));
                                bufferedWriter.write(name + ", " + surname + ", " + telephone_number);
                                bufferedWriter.newLine();
                                bufferedWriter.close();
                                clientWriter.println(print("[" + BLUE + "SUCCESS" + RESET + "]: Friend has been added."));
                            }
                            break;

                        case 2:
                            clientWriter.println(GREEN + print("[=========================================== UPDATING A FRIEND'S DETAILS ===========================================]") + RESET);
                            break;

                        case 3:
                            clientWriter.println(GREEN + print("[=========================================== DELETING A FRIEND ===========================================]") + RESET);
                            clientWriter.println(print("Name of Friend: "));
                            name = clientReader.readLine();
                            System.out.println(name);
                            clientWriter.println(print("Surname of Friend: "));
                            surname = clientReader.readLine();

                            List<String> database_data = new ArrayList<>();

                            Scanner scanner = new Scanner(new File("Database.txt"));
                            while (scanner.hasNextLine())
                                database_data.add(scanner.nextLine());
                            scanner.close();

                            for (String line : database_data) {
                                String[] line_array = line.split(", ");
                                if (line_array[0].equalsIgnoreCase(name) && line_array[1].equalsIgnoreCase(surname)) {
                                    database_data.remove(line);
                                    name = line_array[0];
                                    surname = line_array[1];
                                }
                            }

                            new PrintWriter("Database.txt").close();
                            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("Database.txt", true));

                            for (String line : database_data) {
                                bufferedWriter.write(line);
                                bufferedWriter.newLine();
                                bufferedWriter.close();
                            }

                            clientWriter.println(print("[" + BLUE + "DONE" + RESET + "]: Deleted " + name + " " + surname));
                            break;

                        case 4:
                            clientWriter.println(GREEN + print("[=========================================== LIST OF ALL THE FRIENDS ===========================================]") + RESET);
                            clientWriter.println(print("NAME, SURNAME, TELEPHONE"));
                            scanner = new Scanner(new File("Database.txt"));
                            while (scanner.hasNextLine())
                                clientWriter.println(print( scanner.nextLine()));
                            scanner.close();
                            clientWriter.println(print("[" + BLUE + "DONE" + RESET + "]"));
                            break;

                        case 5:
                            clientWriter.println(GREEN + print("[=========================================== EXITING AND CLOSING CONNECTION ===========================================]") + RESET);
                            System.out.println("Closing Connection");
                            clientWriter.println(print("Thank you for using our services. Closing Connection!"));
                            clientWriter.println(print("[" + BLUE + "SUCCESS" + RESET + "]: Connection closed."));
                            break;
                    }
                    clientWriter.println(print(""));
                }

                clientWriter.close();
                clientReader.close();
                socket.close();
                sever_socket.close();
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public static String print(String text) {
        line_number++;
        return "\033[" + line_number + ";0H" + text + "\033[" + (line_number-1) + ";" + (text.length() + 2) + "H";
    }
}