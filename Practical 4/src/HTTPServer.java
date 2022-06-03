//  Isheanesu Joseph Dzingirai - u20536951
//  Muziwandile Ndlovu - u20469366

import java.net.*;
import java.io.*;

public class HTTPServer {
    public static int client_number = 0;

    public static void main(String ...args) throws IOException {
        int port = 51515;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server has started on port " + port + ".");
        System.out.println("Waiting for client(s)....");
        do {
            Socket socket = serverSocket.accept();
            new ClientHandler(socket).start();
        } while (true);
    }
}
