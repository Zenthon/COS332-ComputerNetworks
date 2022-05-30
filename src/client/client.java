package client;
import java.io.*;
import java.net.*;
import java.util.*;
public class client {
    public static void main(String[] args) {
        String url = "localhost";
        int port = 55540;
        try (Socket pingSocket = new Socket(url, port)) {
          try (PrintWriter out = new PrintWriter(pingSocket.getOutputStream(), true); BufferedReader in = new BufferedReader(new InputStreamReader(pingSocket.getInputStream()));) {
            out.println("ping");
            System.out.println("Telnet Success: " + in.readLine());
          }
        } catch (IOException e) {
          System.out.println("Telnet Fail: " + e.getMessage());
        }
    }
}
