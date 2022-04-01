import java.net.*;
import java.io.*;

public class Server {

    public static int client_number = 0;

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(51515);
        System.out.println("Server running on : " + 51515);

        do {
            Socket socket = serverSocket.accept();
            System.out.println("Client [#" + client_number + " - " + socket.getInetAddress().getHostAddress() + "] connected!");

            BufferedReader clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter clientWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String reqLine = clientReader.readLine();
            String[] reqLineArr = reqLine.split(" ");
            if (reqLineArr[0].equals("GET") && reqLineArr[2].equals("HTTP/1.1")) {
                System.out.println("Valid");
            }

        } while (true);
    }
}