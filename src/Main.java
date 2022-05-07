import java.io.*;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("House Alarm System Activated. Sensors On!");
        System.out.println("The Door is connected to the ENTER key. Opening the door will cause ENTER key to be triggered.");
        System.out.println("Waiting for door to open........");

        System.in.read();
        System.out.println("Door has been opened. Sensors and alarm triggered. Alerting owner");

        String senderEmail = "u20536951@tuks.co.za";
        String receiverEmail = "u20469366@tuks.co.za";

        System.out.println("Sending email.....");
        Socket socket = new Socket("", 25);
        PrintWriter clientWriter = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));


    }
}