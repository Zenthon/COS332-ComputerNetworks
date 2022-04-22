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
            String httpMethod = parseInput.nextToken().toUpperCase(), reqHTML = parseInput.nextToken().toUpperCase(), httpVersion = parseInput.nextToken().toUpperCase(), response = null;

            if (socket.isClosed()) {
                String message = "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Error 400</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "  Socket Is Closed.\n" +
                        "</body>\n" +
                        "</html>";
                response = StatusCode.BadRequest.res + getHeaders(message.length()) + message;
                clientWriter.write(response.getBytes());
            } else if (!httpMethod.equalsIgnoreCase("HEAD") && !httpMethod.equalsIgnoreCase("GET") && !httpMethod.equalsIgnoreCase("POST")) {
                String message = "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Error 501</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "  Only HEAD and POST and GET Requests are allowed.\n" +
                        "</body>\n" +
                        "</html>";
                response = StatusCode.NotImplemented.res + getHeaders(message.length()) + message;
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
                response = StatusCode.httpVersion.res + getHeaders(message.length()) + message;
                clientWriter.write(response.getBytes());
            }
            else if (httpMethod.equalsIgnoreCase("GET")){
                response = StatusCode.OK.res + getHeaders(indexPage().length()) + indexPage();
                clientWriter.write(response.getBytes());
            }
            else if (httpMethod.equalsIgnoreCase("POST")){
                String headerLine = null;
                while((headerLine = clientReader.readLine()).length() != 0)
                    continue;

                StringBuilder payload = new StringBuilder();
                while(clientReader.ready())
                    payload.append((char) clientReader.read());
                String postParameters = payload.toString();

                if (postParameters.equals("option=0")) {
                    response = StatusCode.OK.res + getHeaders(SearchFriendForm().length()) + SearchFriendForm();
                    clientWriter.write(response.getBytes());
                } else if (postParameters.equals("option=1")) {
                    response = StatusCode.OK.res + getHeaders(AddFriendForm().length()) + AddFriendForm();
                    clientWriter.write(response.getBytes());
                } else if (postParameters.equals("option=2")) {
                    response = StatusCode.OK.res + getHeaders(SearchFriendForm().length()) + SearchFriendForm();
                    clientWriter.write(response.getBytes());
                } else if (postParameters.equals("option=3")) {
                    response = StatusCode.OK.res + getHeaders(SearchFriendForm().length()) + SearchFriendForm();
                    clientWriter.write(response.getBytes());
                } else if (postParameters.equals("option=4")) {
                    response = StatusCode.OK.res + getHeaders(SearchFriendForm().length()) + SearchFriendForm();
                    clientWriter.write(response.getBytes());
                } else if (postParameters.equals("option=5")) {
                    String message = "<h1>Thank you for using our services. Closing Connection!</h1>" + "<h1 style=\"color: blue\">[SUCCESS]: Connection closed.";
                    response = StatusCode.OK.res + getHeaders(message.length()) + message;
                    clientWriter.write(response.getBytes());
                    socket.close();
                }

                else if (postParameters.contains("search")) {
                    String [] searchFields = postParameters.split("&");
                    String fname = searchFields[0].split("=").length == 2 ? (searchFields[0].split("="))[1] : "",
                            lname = searchFields[1].split("=").length == 2 ? (searchFields[1].split("="))[1] : "",
                            number = searchFields[2].split("=").length == 2 ? (searchFields[2].split("="))[1] : "",
                            fail = "<h2 style=\"color: red\">[FAILED]: Search failed because Name:" + fname + " Surname: " + lname + " Number: " + number + " is not in the database.</h2>\n",
                            line = null;

                    if ((!number.equals("") && number.matches("0[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]")) || (!fname.equals("") && !lname.equals(""))) {
                        Scanner sc = new Scanner(new File("Database.txt"));
                        while (sc.hasNextLine()) {

                            line = sc.nextLine();
                            System.out.println(line);
                            String []line_array = line.split(", ");
                            if (( (line_array[0]).equalsIgnoreCase(fname) && (line_array[1]).equalsIgnoreCase(lname)) || (line_array[2]).equalsIgnoreCase(number)) {
                                String success = "<h2 style=\"color: blue\">[SUCCESS]: Friend found. Here are the details\t\tName:" + line_array[0] + ", Surname: " + line_array[1] + ", Number: " + line_array[2] + "</h2>\n";
                                response = StatusCode.OK.res + getHeaders(indexPage().length() + success.length()) + (indexPage() + success);
                                clientWriter.write(response.getBytes());
                                break;
                            }
                        }
                        sc.close();
                    }
                    response = StatusCode.OK.res + getHeaders(indexPage().length() + fail.length()) + (indexPage() + fail);
                }
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

    public String indexPage() {
        return "<h1>WELCOME TO MUZI AND ISHEANESU'S SERVER</h1>" +
                "<h2>What would you like to do:</h2>\n" +
                "<form method=\"POST\">\n" +
                    "  <input type=\"radio\" id=\"option\" name=\"option\" value=\"0\">\n" +
                    "  <label for=\"option0\">0: Search a Friend</label><br>\n" +
                    "  <input type=\"radio\" id=\"option\" name=\"option\" value=\"1\">\n" +
                    "  <label for=\"option1\">1: Add a Friend</label><br>\n" +
                    "  <input type=\"radio\" id=\"option\" name=\"option\" value=\"2\">\n" +
                    "  <label for=\"option2\">2: Update Friend's Details</label><br>\n" +
                    "  <input type=\"radio\" id=\"option\" name=\"option\" value=\"3\">\n" +
                    "  <label for=\"option2\">3: Delete a Friend</label><br>\n" +
                    "  <input type=\"radio\" id=\"option\" name=\"option\" value=\"4\">\n" +
                    "  <label for=\"option2\">4: List Friends</label><br>\n" +
                    "  <input type=\"radio\" id=\"option\" name=\"option\" value=\"5\">\n" +
                    "  <label for=\"option2\">5: Exit</label><br>\n" +
                    "  <input type=\"submit\" value=\"Submit\"></center>\n" +
                "</form>";
    }

    public String SearchFriendForm() {
        return "<h2>Please enter the name and surname / the telephone number / both of the friend you are looking for:</h2>\n" +
                "<form method=\"POST\">\n" +
                "  <label for=\"search_fname\">First Name:</label>\n" +
                "  <input type=\"text\" id=\"search_fname\" name=\"search_fname\" value=\"\"><br>\n" +
                "  <label for=\"search_lname\">Last Name:</label>\n" +
                "  <input type=\"text\" id=\"search_lname\" name=\"search_lname\" value=\"\"><br>\n" +
                "  <label for=\"search_number\">Telephone Number:</label>\n" +
                "  <input type=\"text\" id=\"search_number\" name=\"search_number\" value=\"\"><br>\n" +
                "  <input type=\"submit\" value=\"Submit\"></center>\n" +
                "</form>";
    }

    public String AddFriendForm() {
        return null;
    }
}
