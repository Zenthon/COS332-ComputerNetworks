import java.io.*;
import java.net.Socket;
import java.util.*;


public class ClientHandler extends Thread {
    public final Socket socket;
    public final BufferedReader clientReader;
    public final PrintWriter clientWriter;
    public int clientNumber;

    public static final String GREEN = "\u001B[32m";
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001b[35m";

    public static String details ,name, surname, telephone_number;
    public int line_number = 0;
    public static boolean Found = false;

    public ClientHandler(Socket socket, int cNumber, BufferedReader clientReader, PrintWriter clientWriter) {
        this.clientReader = clientReader;
        this.clientWriter = clientWriter;
        this.socket = socket;
        this.clientNumber = cNumber;
    }

    @Override
    public void run() {
        // Database operations
        String[] options = {"    0. Search a Friend", "    1. Add a Friend", "    2. Update Friend's Details", "    3. Delete a Friend", "    4. List Friends", "    5. Exit"};
        int choice = -1;
        clientWriter.println(MAGENTA + print("*************************************************************************************"));
        clientWriter.println(print("\t\t\tWELCOME TO MUZI AND ISHEANESU'S SERVER"));
        clientWriter.println(print("*************************************************************************************"));
        clientWriter.println(print("") + RESET);

        while (choice != 5) {
            clientWriter.println(print("What would you like to do:"));
            for (String s : options) clientWriter.println(print(s));
            clientWriter.println(print(""));

            //  Read choice
            do {
                clientWriter.println(print("Choice:"));
                String str_choice = null;
                try { str_choice = clientReader.readLine(); }
                catch (IOException e) { e.printStackTrace(); }
                choice = (str_choice.matches("[0-9]+")) ? Integer.parseInt(str_choice) : -1;
            } while (choice < 0 || choice > 5);

            clientWriter.println("\033[2J");
            line_number = 0;
            clientWriter.println("\033[0;0H");

            switch (choice) {
                case 0:
                    clientWriter.print(GREEN + print("[====================================== SEARCHING FOR FRIEND ======================================]") + RESET);
                    sd_prompt("Please select 0 to search by name and surname or 1 to search by telephone: ");
                    String line = contains();
                    if (line != null)
                        clientWriter.println(print("[" + BLUE + "SUCCESS" + RESET + "]: Friend found. Here are the details: " + line));
                    else {
                        String answer = !telephone_number.equals("NA") ? "Telephone number: " + telephone_number : "Name and Surname: " + (name + " " + surname);
                        clientWriter.println(print("[" + RED + "SUCCESS" + RESET + "]: Search failed because " + answer + " is not in the database."));
                    }
                    break;

                case 1:
                    clientWriter.println(GREEN + print("[====================================== ADDING A FRIEND ======================================]") + RESET);
                    prompt();
                    if (contains() != null)
                        clientWriter.println(print("[" + RED + "FAILED" + RESET + "]: " + name + " " + surname + " already exits / telephone number is already used by someone else."));
                    else if (!name.matches("[a-zA-Z]+") || !surname.matches("[a-zA-Z]+") || !(telephone_number.matches("[0-9]+") && telephone_number.length() == 10))
                        clientWriter.println(print("[" + RED + "FAILED" + RESET + "]: Could not add friend because the name / surname is not alpha or the telephone is not numeric and 10 digits."));
                    else {
                        try {
                            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("Database.txt", true));
                            bufferedWriter.write(name + ", " + surname + ", " + telephone_number);
                            bufferedWriter.newLine();
                            bufferedWriter.close();
                            clientWriter.println(print("[" + BLUE + "SUCCESS" + RESET + "]: Friend has been added."));
                        } catch (IOException e) {
                            System.out.println("Database.txt not found. Adding a student failed");
                        }
                    }
                    break;

                case 2:
                    clientWriter.println(GREEN + print("[====================================== UPDATING A FRIEND'S DETAILS ======================================]") + RESET);
                    clientWriter.println(print("Please enter the name / surname / telephone number of the person you want to update: "));
                    String response = search();
                    if (response == null) {
                        break;
                    } else {
                        clientWriter.println(print("Please enter the updated Record: "));
                
                            prompt();
                            if (!name.matches("[a-zA-Z]+") || !surname.matches("[a-zA-Z]+") || !(telephone_number.matches("[0-9]+") && telephone_number.length() == 10)) {
                                clientWriter.println(print("[" + RED + "FAILED" + RESET + "]: Could not add friend because the name / surname is not alpha or the telephone is not numeric."));
                                clientWriter.println(print(""));
                                break;
                            }
                      

                        try {
                            String Record = name + ", " + surname + ", " + telephone_number;
                            Scanner sc = new Scanner(new File("Database.txt"));
                            StringBuffer buffer = new StringBuffer();
                            while (sc.hasNextLine()) {
                                buffer.append(sc.nextLine() + System.lineSeparator());
                            }
                            String fileContents = buffer.toString();
                            sc.close();
                            fileContents = fileContents.replaceAll(response, Record);
                            FileWriter writer = new FileWriter("Database.txt");
                            writer.append(fileContents);
                            writer.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case 3:
                    clientWriter.println(GREEN + print("[====================================== DELETING A FRIEND ======================================]") + RESET);
                    sd_prompt("Please select 0 to delete by name and surname or 1 to delete by telephone: ");
                    String delete_line = contains(), answer = !telephone_number.equals("NA") ? "Telephone number: " + telephone_number : "Name and Surname: " + (name + " " + surname);
                    if (delete_line != null) {
                        try {
                            List<String> database_data = new ArrayList<>();
                            Scanner scanner = new Scanner(new File("Database.txt"));
                            while (scanner.hasNextLine())
                                database_data.add(scanner.nextLine());
                            scanner.close();

                            for (int index = 0; index < database_data.size(); index++)
                                if (database_data.get(index) != "" && database_data.get(index) != null && database_data.get(index).equalsIgnoreCase(delete_line))
                                    database_data.remove(index);

                            new PrintWriter("Database.txt").close();
                            if (!database_data.isEmpty()) {
                                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("Database.txt", true));

                                for (String l : database_data) {
                                    bufferedWriter.write(l);
                                    bufferedWriter.newLine();
                                    bufferedWriter.close();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        clientWriter.println(print("[" + BLUE + "SUCCESS" + RESET + "]: Deleted " + answer));
                    }
                    else clientWriter.println(print("[" + RED + "FAILURE" + RESET + "]: Could not find " + answer + " in the database."));

                    break;

                case 4:
                    clientWriter.println(GREEN + print("[====================================== LIST OF ALL THE FRIENDS ======================================]") + RESET);
                    clientWriter.println(print("NAME, SURNAME, TELEPHONE"));
                    int numFriends = 0;
                    try {
                        Scanner scanner = new Scanner(new File("Database.txt"));
                        while (scanner.hasNextLine()) {
                            clientWriter.println(print(scanner.nextLine()));
                            numFriends++;
                        }
                        scanner.close();
                        clientWriter.println(print("[" + BLUE + "SUCCESS" + RESET + "]: Listed " + numFriends + " friend(s)"));
                    } catch (IOException e) {
                        System.out.println("Scanner failed to open file in case 4");
                    }
                    break;

                case 5:
                    clientWriter.println(GREEN + print("[====================================== EXITING AND CLOSING CONNECTION ======================================]") + RESET);
                    System.out.println("Closing Connection for client " + clientNumber);
                    clientWriter.println(print("Thank you for using our services. Closing Connection!"));
                    clientWriter.println(print("[" + BLUE + "SUCCESS" + RESET + "]: Connection closed."));
                    try { socket.close(); } catch (IOException e) { System.out.println("Could not close socket for client #" + clientNumber); }
                    break;
            }
            clientWriter.println(print(""));
        }
    }

    public String print(String text) {
        line_number++;
        return "\033[" + line_number + ";0H" + text + "\033[" + (line_number-1) + ";" + (text.length() + 2) + "H";
    }

    public String search(){
        try {
            details = clientReader.readLine();
        }catch (IOException e) {
            e.printStackTrace();
        }

        File file  = new File("Database.txt");
        String line = null;
        int lineNum = 0;
        Found = false;
        try{
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                line = scanner.nextLine();
                lineNum++;

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
                if(Found){
                    break;
                }
                if(!scanner.hasNextLine()){
                    line = null;
                    clientWriter.println(print("[" + RED + "FAILURE" + RESET +"]: Friend has not been found."));
                    clientWriter.print(print(""));
                }
            }
            scanner.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        return line;
    }

    public void prompt() {
        try {
            clientWriter.println(print("Name of Friend: "));
            name = clientReader.readLine();
            clientWriter.println(print("Surname of Friend: "));
            surname = clientReader.readLine();
            clientWriter.println(print("Telephone Number of Friend: "));
            telephone_number = clientReader.readLine();
        } catch (IOException e) {
            System.out.println("Prompt function ran into an error with the readline!");
        }
    }

    public String contains(){
        try {
            Scanner scanner = new Scanner(new File("Database.txt"));
            String line = null;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                String []line_array = line.split(", ");
                if (( (line_array[0]).equalsIgnoreCase(name) && (line_array[1]).equalsIgnoreCase(surname)) || (line_array[2]).equalsIgnoreCase(telephone_number))
                    return line;
            }
        } catch (IOException e) {
            System.out.println("Contains function failed because could not find Database.txt");
        }
        return null;
    }

    public void sd_prompt(String message) {
        int search_choice = -1;
        do {
            clientWriter.println(print(message));
            String ss = null;
            try { ss = clientReader.readLine(); }
            catch (IOException e) { e.printStackTrace(); }
            search_choice = (ss.matches("[0-9]+")) ? Integer.parseInt(ss) : -1;
        } while (search_choice < 0 || search_choice > 1);

        try {
            if (search_choice == 0) {
                clientWriter.println(print("Name of Friend: "));
                name = clientReader.readLine();
                clientWriter.println(print("Surname of Friend: "));
                surname = clientReader.readLine();
                telephone_number = "NA";
            }

            else {
                surname = name = "NA";
                clientWriter.println(print("Telephone Number of Friend: "));
                telephone_number = clientReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}