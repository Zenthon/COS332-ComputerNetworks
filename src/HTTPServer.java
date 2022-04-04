//  Isheanesu Joseph Dzingirai - u20536951
//  Muziwandile Ndlovu - u20469366

import java.io.*;
import java.net.*;
import java.util.*;

public class HTTPServer {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(50505);
            System.out.println("Server running on : " + 50505);
            System.out.println("Waiting for client....");

            while (true) {
                Socket socket = serverSocket.accept();
                new HTTPClientHandler(socket).start();
            }
        } catch (IOException e) {
           e.printStackTrace();
        }
    }
}