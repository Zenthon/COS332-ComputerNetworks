import java.net.*;
import java.io.*;

public class Server {

    public static int client_number = 0;

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(55555);
        System.out.println("Server running on : " + 55555);

        do {
            Socket socket = serverSocket.accept();
            System.out.println("Client [#" + client_number + " - " + socket.getInetAddress().getHostAddress() + "] connected!");

            BufferedReader clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter clientWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String request_line = clientReader.readLine();
            System.out.println(request_line);

        } while (true);
    }
}