//  Isheanesu Joseph Dzingirai - u20536951
//  Muziwandile Ndlovu - u20469366

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.*;
import java.net.*;


public class HTTPServer {
    static ScriptEngineManager manager = new ScriptEngineManager();
    static ScriptEngine engine = manager.getEngineByName("js");

    public static void main(String[] args) {
        System.out.print("\033[H\033[2J");
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(52525);
            System.out.println("Server running on : " + 52525);
            System.out.println("Waiting for client....");

            while (true) {
                Socket socket = serverSocket.accept();
                new HTTPClientHandler(socket, engine).start();
            }
        } catch (IOException e) {
           e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}