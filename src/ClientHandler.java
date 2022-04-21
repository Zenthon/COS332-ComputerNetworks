//  Isheanesu Joseph Dzingirai - u20536951
//  Muziwandile Ndlovu - u20469366

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;




public class ClientHandler extends Thread {
    public enum StatusCode {
        OK("HTTP/1.1 200 OK \r\n"),
        BadRequest("HTTP/1.1 400 Bad Request \r\n"),
        httpVersion("HTTP/1.1 505 HTTP Version Not Supported \r\n"),
        NotImplemented("HTTP/1.1 501 Not Implemented \r\n");
        private String res;

        StatusCode(String response) {
            this.res = response;
        }
    }


    public final Socket socket;
    public final BufferedReader clientReader;
    public final OutputStream clientWriter;
    public int clientNumber;

    public static final String GREEN = "\u001B[32m";
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001b[35m";

    public static String details, name, surname, telephone_number;
    public int line_number = 0;
    public static boolean Found = false;

    public ClientHandler(Socket socket, int cNumber) throws IOException {
        this.clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.clientWriter = socket.getOutputStream();
        this.socket = socket;
        this.clientNumber = cNumber;
    }

    @Override
    public void run() {
        try {
            StringTokenizer parseInput = new StringTokenizer(clientReader.readLine());
            String httpMethod = parseInput.nextToken().toUpperCase(), reqHTML = parseInput.nextToken().toUpperCase(), httpVersion = parseInput.nextToken().toUpperCase();
            if (!httpMethod.equals("HEAD") && !httpMethod.equals("POST")) {
                String message = "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Error 501</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "  Only HEAD and POST Requests are allowed.\n" +
                        "</body>\n" +
                        "</html>";
                String response = StatusCode.NotImplemented.res + getHeaders(message.length()) + message;
                clientWriter.write(response.getBytes());
            } else if (!httpVersion.equals("HTTP/1.1")) {
                String message = "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Error 505</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "  Only HTTP 1.1 Is allowed\n" +
                        "</body>\n" +
                        "</html>";
                String response = StatusCode.httpVersion.res + getHeaders(message.length()) + message;
                clientWriter.write(response.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getHeaders(int contentLength) {
        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("E, MM dd yyyy HH:mm:ss");
        return "Server: Muzi and Ishe (WSL)\r\n" +
                "Allow: GET, HEAD\r\n" +
                "Date: " + currentDate.format(dateFormatter) + " GMT\r\n" +
                "Server: Muzi and Ishe's Server\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + contentLength + "\r\n" +
                "Connection: keep-alive\r\n" +
                "\r\n";
    }
}
