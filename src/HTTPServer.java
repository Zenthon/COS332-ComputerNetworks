//  Isheanesu Joseph Dzingirai - u20536951
//  Muziwandile Ndlovu - u20469366

import java.io.*;
import java.net.*;
import java.util.*;

public class HTTPServer {
    public static int client_number = 1;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(51515);
            System.out.println("Server running on : " + 51515);
            System.out.println("Waiting for client....");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client " + socket.getInetAddress().getHostAddress() + "] connected!");
                new HTTPClientHandler(socket).start();
            }
        } catch (IOException e) {
           e.printStackTrace();
        }
    }
}