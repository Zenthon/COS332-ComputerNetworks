import java.net.*;
import java.io.*;

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
        String details = "";

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

                    switch (choice) {
                        case 0:
                            clientWriter.print(GREEN + print("[=========================================== SEARCHING FOR FRIEND ===========================================]") + RESET);
                            clientWriter.println(print("Please enter the name and surname / telephone number of the person you are looking for: "));
                            details = clientReader.readLine();
                            break;

                        case 1:
                            clientWriter.println(GREEN + print("[=========================================== ADDING A FRIEND ===========================================]") + RESET);
                            clientWriter.println(print("Please enter the name, surname and telephone number separated by a space of your friend: "));
                            details = clientReader.readLine();
                            System.out.println(details);
                            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("Database.txt", true));
                            bufferedWriter.write(details.replace(" ", ", "));
                            bufferedWriter.newLine();
                            bufferedWriter.close();
                            clientWriter.println(print("[" + BLUE + "SUCCESS" + RESET +"]: Friend has been added."));
                            clientWriter.println(print(""));
                            break;

                        case 3:
                            clientWriter.println(GREEN + print("[=========================================== UPDATING A FRIEND'S DETAILS ===========================================]") + RESET);
                            break;

                        case 4:
                            clientWriter.println(GREEN + print("[=========================================== LIST OF ALL THE FRIENDS ===========================================]") + RESET);
                            clientWriter.println("NAME, SURNAME, TELEPHONE");

                            break;

                        case 5:
                            clientWriter.println(GREEN + print("[=========================================== EXITING AND CLOSING CONNECTION ===========================================]"));
                            System.out.println("Closing Connection");
                            break;
                    }
                }

                clientWriter.close();
                clientReader.close();
                socket.close();
                sever_socket.close();
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