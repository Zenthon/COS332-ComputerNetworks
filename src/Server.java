import java.net.*;
import java.io.*;

public class Server {
    public static int client_number = 0;


    public static void main(String ...args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(55555);
        System.out.println("Server has started.");
        System.out.println("Waiting for client(s)....");

        do {
            Socket socket = serverSocket.accept();
            System.out.println("Client [" + client_number + " - " + socket.getInetAddress().getHostAddress() + "] connected!");
            BufferedReader clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter clientWriter = new PrintWriter(socket.getOutputStream(),true);

            System.out.println("Assigning new thread for " + client_number);

            Thread t = new ClientHandler(client_number++, socket, clientReader, clientWriter);
            t.start();
        } while (client_number != 0);
    }
}