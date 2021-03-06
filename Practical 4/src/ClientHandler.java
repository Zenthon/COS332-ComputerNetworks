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
    public String name, surname, telephone_number;

    public ClientHandler(Socket socket) throws IOException {
        this.clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.clientWriter = socket.getOutputStream();
        this.socket = socket;
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
                        "  Bad Request: Socket Is Closed.\n" +
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
                    response = StatusCode.OK.res + getHeaders(UpdateFriendForm().length()) + UpdateFriendForm();
                    clientWriter.write(response.getBytes());
                } else if (postParameters.equals("option=3")) {
                    response = StatusCode.OK.res + getHeaders(DeleteFriendForm().length()) + DeleteFriendForm();
                    clientWriter.write(response.getBytes());
                } else if (postParameters.equals("option=4")) {
                    response = StatusCode.OK.res + getHeaders(ListFriendForm().length()) + ListFriendForm();
                    clientWriter.write(response.getBytes());
                } else if (postParameters.equals("option=5")) {
                    String message = "<h1>Thank you for using our services. Closing Connection!</h1>" + "<h1 style=\"color: blue\">[SUCCESS]: Connection closed.";
                    response = StatusCode.OK.res + getHeaders(message.length()) + message;
                    clientWriter.write(response.getBytes());
                    socket.close();
                }

                // 1. Search Friend
                else if (postParameters.contains("search")) {
                    extractParameters(postParameters);
                    String fail = "<h2 style=\"color: red\">[FAILED]: Search failed because friend details are not in the database</h2>";
                    if (telephone_number.matches("0[0-9]{9}") || telephone_number.equals("") || (!name.equals("") && !surname.equals(""))) {
                        Scanner sc = new Scanner(new File("Database.txt"));
                        while (sc.hasNextLine()) {
                            String []line_array = (sc.nextLine()).split(", ");
                            if (((line_array[0]).equalsIgnoreCase(name) && (line_array[1]).equalsIgnoreCase(surname)) || (line_array[2]).equalsIgnoreCase(telephone_number)) {
                                String success = "<h2 style=\"color: blue\">[SUCCESS]: Friend found. Here are the details\t\tName:" + line_array[0] + ", Surname: " + line_array[1] + ", Number: " + line_array[2] + "</h2>\n";
                                response = StatusCode.OK.res + getHeaders(indexPage().length() + success.length()) + (indexPage() + success);
                                clientWriter.write(response.getBytes());
                                break;
                            }
                        }
                        sc.close();
                    }
                    response = StatusCode.OK.res + getHeaders(indexPage().length() + fail.length()) + (indexPage() + fail);
                    clientWriter.write(response.getBytes());
                }
                else if (postParameters.contains("update")) {
                    String [] searchFields = postParameters.split("&");
                    String fname = searchFields[0].split("=").length == 2 ? (searchFields[0].split("="))[1] : "",
                            lname = searchFields[1].split("=").length == 2 ? (searchFields[1].split("="))[1] : "",
                            number = searchFields[2].split("=").length == 2 ? (searchFields[2].split("="))[1] : "",
                            fail = "<h2 style=\"color: red\">[FAILED]: Search failed because Name:" + fname + " Surname: " + lname + " Number: " + number + " is not in the database.</h2>\n",
                            line = null;

                    if ((!number.equals("") && number.matches("0[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]")) || (!fname.equals("") && !lname.equals(""))) {
                        Scanner sc = new Scanner(new File("Database.txt"));
                        File textFile = new File("Database.txt");
                        searchFields = postParameters.split("&");
                        while (sc.hasNextLine()) {

                            line = sc.nextLine();
                            System.out.println(line);
                            String []line_array = line.split(", ");
                            if  (( (line_array[0]).equalsIgnoreCase(fname) && (line_array[1]).equalsIgnoreCase(lname)) || (line_array[2]).equalsIgnoreCase(number)) {
                                String Original = fname + ", "+lname+ ", "+number;
                                fname = searchFields[3].split("=").length == 2 ? (searchFields[3].split("="))[1] : "";
                                lname = searchFields[4].split("=").length == 2 ? (searchFields[4].split("="))[1] : "";
                                number = searchFields[5].split("=").length == 2 ? (searchFields[5].split("="))[1] : "";
                                String myReplacement = fname + ", "+lname+ ", "+number;
                                System.out.println(myReplacement);
                                System.out.println(Original);
                                BufferedReader reader = null;
                                String Content ="";
                                FileWriter writer = null;
                                try
                                {
                                    reader = new BufferedReader(new FileReader(textFile));
                                    String lines = reader.readLine();
                                    while (lines != null) 
                                    {
                                        Content=Content + lines + System.lineSeparator();
                                        lines = reader.readLine();
                                    }
                                    System.out.println(Content);
                                    String newContent = Content.replaceAll(Original, myReplacement  );
                                    writer = new FileWriter("Database.txt");
                                    System.out.println(newContent);
                                    writer.write(newContent);
                                }
                                catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                                finally
                                {
                                    try
                                    {
                                        reader.close();   
                                        writer.close();
                                    } 
                                    catch (IOException e) 
                                    {
                                        e.printStackTrace();
                                    }
                                }
                                String success = "<h2 style=\"color: blue\">[SUCCESS]: Friend Updated. Here are the details\t\tName:" + fname  + ", Surname: " + lname + ", Number: " +number + "</h2>\n";

                                response = StatusCode.OK.res + getHeaders(indexPage().length() + success.length()) + (indexPage() + success);
                                clientWriter.write(response.getBytes());
                                break;
                            }
                        }
                        sc.close();
                    }
                    response = StatusCode.OK.res + getHeaders(indexPage().length() + fail.length()) + (indexPage() + fail);
                    clientWriter.write(response.getBytes());
                }

                // 2. Add Friend
                else if (postParameters.contains("add")) {

                    extractParameters(postParameters);

                    if (!telephone_number.matches("0[0-9]{9}") || !name.matches("[a-zA-Z]+") || !surname.matches("[a-zA-Z]+") ) {
                        String message = "<h2 style=\"color: red\">[FAILED]: Could not add friend because the name / surname is not alpha or the telephone is not numeric and 10 digits</h2>\n";
                        response = StatusCode.OK.res + getHeaders(indexPage().length() + message.length()) + (indexPage() + message);
                        clientWriter.write(response.getBytes());
                    }
                    else if (searchFriend()) {
                        String message = "<h2 style=\"color: red\">[FAILED]: " + name + " " + surname + " / " + telephone_number + " already exists in the database</h2>\n";
                        response = StatusCode.OK.res + getHeaders(indexPage().length() + message.length()) + (indexPage() + message);
                        clientWriter.write(response.getBytes());
                    }
                    else {
                        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("Database.txt", true));
                        bufferedWriter.write(name + ", " + surname + ", " + telephone_number);
                        bufferedWriter.newLine();
                        bufferedWriter.close();
                        String message = "<h1 style=\"color: blue\">[SUCCESS]: Friend has been added.";
                        response = StatusCode.OK.res + getHeaders(indexPage().length() + message.length()) + (indexPage() + message);
                        clientWriter.write(response.getBytes());
                    }
                }
                else if (postParameters.contains("delete")) {
                    extractParameters(postParameters);
                    String fail = "<h2 style=\"color: red\">[FAILED]: Search failed because friend details are not in the database</h2>";
                    if (telephone_number.matches("0[0-9]{9}") || telephone_number.equals("") || (!name.equals("") && !surname.equals(""))) {
                        Scanner sc = new Scanner(new File("Database.txt"));
                        while (sc.hasNextLine()) {
                            String []line_array = (sc.nextLine()).split(", ");
                            if (((line_array[0]).equalsIgnoreCase(name) && (line_array[1]).equalsIgnoreCase(surname)) && (line_array[2]).equalsIgnoreCase(telephone_number)) {
                                File inputFile = new File("Database.txt");
                                File tempFile = new File("myTempFile.txt");
                                String [] searchFields = postParameters.split("&");
                                String fname = searchFields[0].split("=").length == 2 ? (searchFields[0].split("="))[1] : "",
                                lname = searchFields[1].split("=").length == 2 ? (searchFields[1].split("="))[1] : "",
                                number = searchFields[2].split("=").length == 2 ? (searchFields[2].split("="))[1] : "";
                                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
                                String lineToRemove = fname + ", "+lname+ ", "+number;
                                StringBuilder sb = new StringBuilder();
                                try (Scanner sk = new Scanner(inputFile)) {
                                    String currentLine;
                                    while(sk.hasNext()){
                                        currentLine = sk.nextLine();
                                        if(currentLine.equals(lineToRemove)){
                                            continue; 
                                        }
                                        sb.append(currentLine).append("\n");
                                    }
                                }
                                PrintWriter pw = new PrintWriter(inputFile);
                                pw.close();
                                writer = new BufferedWriter(new FileWriter(inputFile, true));
                                writer.append(sb.toString());
                                writer.close();
                                String success = "<h2 style=\"color: blue\">[SUCCESS]: Friend Delete. Here are the details\t\tName:" + line_array[0] + ", Surname: " + line_array[1] + ", Number: " + line_array[2] + "</h2>\n";
                                response = StatusCode.OK.res + getHeaders(indexPage().length() + success.length()) + (indexPage() + success);
                                clientWriter.write(response.getBytes());
                                break;
                            }
                        }
                        
                        sc.close();
                    }

                    response = StatusCode.OK.res + getHeaders(indexPage().length() + fail.length()) + (indexPage() + fail);
                    clientWriter.write(response.getBytes());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void extractParameters(String postParameters) {
        String [] searchFields = postParameters.split("&");
        name = searchFields[0].split("=").length == 2 ? (searchFields[0].split("="))[1] : "";
        surname = searchFields[1].split("=").length == 2 ? (searchFields[1].split("="))[1] : "";
        telephone_number = searchFields[2].split("=").length == 2 ? (searchFields[2].split("="))[1] : "";
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
                    "  <label for=\"option3\">3: Delete a Friend</label><br>\n" +
                    "  <input type=\"radio\" id=\"option\" name=\"option\" value=\"4\">\n" +
                    "  <label for=\"option4\">4: List Friends</label><br>\n" +
                    "  <input type=\"radio\" id=\"option\" name=\"option\" value=\"5\">\n" +
                    "  <label for=\"option5\">5: Exit</label><br>\n" +
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
                "  <input type=\"submit\" class=\"btn btn-primary\" value=\"Submit\"></center>\n" +
                "</form>";
    }
    public String  ListFriendForm() throws IOException{
        String res = "<h2 style=\"color: blue\"> Here are the details of Friends in Database\t\tName:"  + "</h2>\n";
                File inputFile = new File("Database.txt");
                try (Scanner sk = new Scanner(inputFile)) {
                    String currentLine;
                    while(sk.hasNext()){
                        currentLine = sk.nextLine();
                        res +="<h2 style=\"color: blue\">" + currentLine + "</h2>\n";
                    }
                }
                return res;   
   }
  

    public String AddFriendForm() {
        return "<h2>Please enter the name, surname, and telephone number of your friend</h2>\n" +
                "<form method=\"POST\">\n" +
                "  <label for=\"add_fname\">First Name:</label>\n" +
                "  <input type=\"text\" id=\"add_fname\" name=\"add_fname\" value=\"\"><br>\n" +
                "  <label for=\"add_lname\">Last Name:</label>\n" +
                "  <input type=\"text\" id=\"add_lname\" name=\"add_lname\" value=\"\"><br>\n" +
                "  <label for=\"add_number\">Telephone Number:</label>\n" +
                "  <input type=\"text\" id=\"add_number\" name=\"add_number\" value=\"\"><br>\n" +
                "  <input type=\"submit\" class=\"btn btn-primary\" value=\"Submit\"></center>\n" +
                "</form>";
    }

    public boolean searchFriend() throws FileNotFoundException {
        String line;
        Scanner sc = new Scanner(new File("Database.txt"));
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            String []line_array = line.split(", ");
            if (( (line_array[0]).equalsIgnoreCase(name) && (line_array[1]).equalsIgnoreCase(surname)) || (line_array[2]).equalsIgnoreCase(telephone_number)) {
                sc.close();
                return true;
            }
        }
        sc.close();
        return false;
    }
    public String UpdateFriendForm() {
            return "<h2>Please enter the name ,surname and the telephone number of you are looking to Update Along With The updated Information:</h2>\n" +
                "<form method=\"POST\">\n" +
                "  <label for=\"fname\">First Name:</label>\n" +
                "  <input type=\"text\" id=\"fname\" name=\"fname\" value=\"\"><br>\n" +
                "  <label for=\"lname\">Last Name:</label>\n" +
                "  <input type=\"text\" id=\"lname\" name=\"lname\" value=\"\"><br>\n" +
                "  <label for=\"number\">Telephone Number:</label>\n" +
                "  <input type=\"text\" id=\"number\" name=\"number\" value=\"\"><br>\n" +
                "  <label for=\"update_fname\">First Name Update:</label>\n" +
                "  <input type=\"text\" id=\"update_fname\" name=\"update_fname\" value=\"\"><br>\n" +
                "  <label for=\"update_lname\">Last Name Update:</label>\n" +
                "  <input type=\"text\" id=\"update_lname\" name=\"update_lname\" value=\"\"><br>\n" +
                "  <label for=\"update_number\">Telephone Number Update:</label>\n" +
                "  <input type=\"text\" id=\"update_number\" name=\"update_number\" value=\"\"><br>\n" +
                "  <input type=\"submit\" value=\"Submit\"></center>\n" +
                "</form>";
    }

    public String DeleteFriendForm() {
        return "<h2>Please enter the name, surname and  the telephone number of the friend you are looking to delete:</h2>\n" +
        "<form method=\"POST\">\n" +
        "  <label for=\"delete_fname\">First Name:</label>\n" +
        "  <input type=\"text\" id=\"delete_fname\" name=\"delete_fname\" value=\"\"><br>\n" +
        "  <label for=\"lname\">Last Name:</label>\n" +
        "  <input type=\"text\" id=\"delete_lname\" name=\"delete_lname\" value=\"\"><br>\n" +
        "  <label for=\"delete_number\">Telephone Number:</label>\n" +
        "  <input type=\"text\" id=\"delete_number\" name=\"delete_number\" value=\"\"><br>\n" +
        "  <input type=\"submit\" value=\"Submit\"></center>\n" +
        "</form>";
    }
}
