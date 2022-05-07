import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateobj = new Date();

        System.out.println("House Alarm System Activated. Sensors On!");
        System.out.println("The Door is connected to the ENTER key. Opening the door will cause ENTER key to be triggered.");
        System.out.println("Waiting for door to open........");

        System.in.read();
        System.out.println("Door has been opened. Sensors and alarm triggered. Alerting owner");

        System.out.println("Sending email.....");
        Socket socket = ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket("smtp.gmail.com", 465);
         OutputStream clientWriter = socket.getOutputStream();
        BufferedReader clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));


        // Greet the server
        clientWriter.write("EHLO gmail.com\r\n".getBytes());

        // Authenticating email address of sender
        clientWriter.write("AUTH LOGIN\r\n".getBytes());
        Thread.sleep(3000);
        clientWriter.write("c2VuZGVyLmdtbDAxQGdtYWlsLmNvbQ==\r\n".getBytes());
        Thread.sleep(3000);
        clientWriter.write("SXNoZWFuZXN1QDE=\r\n".getBytes());

        // Mail from
        clientWriter.write("MAIL FROM:<sender.gml01@gmail.com>\r\n".getBytes());

        // Mail to
        clientWriter.write("RCPT TO:<u20469366@tuks.co.za>\r\n".getBytes());

        // Data
        clientWriter.write("DATA\r\n".getBytes());
        Thread.sleep(3000);
        clientWriter.write("SUBJECT: Home Alarm Triggered!\r\n".getBytes());
        clientWriter.write("Good day Ishe,\r\n".getBytes());
        String message = "Please note that the alarm system to the house was triggered at " + df.format(dateobj) + ".\r\nSomeone is trying/ has already broken into the house.\r\nPlease call 10111.\r\n";
        clientWriter.write(message.getBytes());
        clientWriter.write("\r\n".getBytes());
        clientWriter.write("Kind Regards,\r\n".getBytes());
        clientWriter.write("Alarm System Management\r\n".getBytes());
        clientWriter.write(".\r\n".getBytes());

        // QUIT
        clientWriter.write("QUIT".getBytes());

        String line;
        while ((line = clientReader.readLine()) != null)
            System.out.println(line);

        clientWriter.close();
        clientReader.close();
        socket.close();
    }
}