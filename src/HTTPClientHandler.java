//  Isheanesu Joseph Dzingirai - u20536951
//  Muziwandile Ndlovu - u20469366

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;

public class HTTPClientHandler extends Thread {
    private Socket socket;
    private BufferedReader clientReader;
    private OutputStream clientWriter;
    private static String answer = "0", response ="", expression ="";

    public HTTPClientHandler(Socket s) throws IOException {
        socket = s;
        clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        clientWriter = socket.getOutputStream();
    }

    @Override
    public void run() {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("js");

            StringTokenizer parseInput = new StringTokenizer(clientReader.readLine());
            String httpMethod = parseInput.nextToken().toUpperCase(), reqHTML = parseInput.nextToken().toUpperCase(), httpVersion = parseInput.nextToken().toUpperCase();
            if (!httpMethod.equals("GET") && !httpMethod.equals("HEAD")) {
                String message = "<!DOCTYPE html>\n" +
                                    "<html lang=\"en\">\n" +
                                    "<head>\n" +
                                    "    <meta charset=\"UTF-8\">\n" +
                                    "    <title>Error 501</title>\n" +
                                    "</head>\n" +
                                    "<body>\n" +
                                    "  Only HEAD and GET Requests are allowed\n" +
                                    "</body>\n" +
                                    "</html>";
                String response = getStatusLine(501, "Not Implemented") + getHeaders(message.length()) + message;
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
                String response = getStatusLine(505, "HTTP Version Not Supported") + getHeaders(message.length()) + message;
                clientWriter.write(response.getBytes());
            } else {
                if (reqHTML.equals("/"))
                    response = getStatusLine(200, "OK") + getHeaders(getCalculator().length()) + getCalculator();
                else if (reqHTML.equals("/C")){
                    answer = "0";
                    expression = "";
                    response = getStatusLine(200, "OK") + getHeaders(getCalculator().length()) + getCalculator();
                } else if (reqHTML.equals("/=")) {
                    answer = String.valueOf(engine.eval(expression));
                    response = answer.equalsIgnoreCase("INFINITY") ?
                                getStatusLine(400, "Bad Request") + getHeaders(getCalculator().length() + 32) + "<p>Division by zero not allowed." + getCalculator() :
                                getStatusLine(200, "OK") + getHeaders(getCalculator().length()) + getCalculator();
                }
                else {
                    if (!reqHTML.equals("/DIV"))
                        expression += reqHTML.charAt(1);
                    else expression += "/";
                    answer = expression;
                    response = getStatusLine(200, "OK") + getHeaders(getCalculator().length()) + getCalculator();
                }
                clientWriter.write(response.getBytes());
            }
        } catch (IOException | ScriptException e) {
            e.printStackTrace();
        }
    }

    public  String getStatusLine(int statusCode, String reasonPhrase) {
        return "HTTP/1.1 " + statusCode + " " + reasonPhrase + "\r\n";
    }

    public String getHeaders(int contentLength) {
        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("E, MM dd yyyy HH:mm:ss");
        return  "Server: Muzi and Ishe (WSL)\r\n" +
                "Allow: GET, HEAD\r\n" +
                "Date: " + currentDate.format(dateFormatter) + " GMT\r\n" +
                "Server: Muzi and Ishe's Server\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + contentLength + "\r\n" +
                "\r\n";
    }

    public String getCalculator() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "   <title>Calculator</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "   <table border=\"1\">\n" +
                "       <tr>\n" +
                "           <p><th colspan=\"4\">" + answer + "</th><p>\n" +
                "       </tr>\n" +
                "       <tr>\n" +
                "           <td><a href =\"/1\"><button>1</button></a></td>\n" +
                "           <td><a href =\"/2\"><button>2</button></a></td>\n" +
                "           <td><a href =\"/3\"><button>3</button></a></td>\n" +
                "           <td><a href =\"/+\"><button>+</button></a></td>\n" +
                "       </tr>\n" +
                "       <tr>\n" +
                "           <td><a href =\"/4\"><button>4</button></a></td>\n" +
                "           <td><a href =\"/5\"><button>5</button></a></td>\n" +
                "           <td><a href =\"/6\"><button>6</button></a></td>\n" +
                "           <td><a href =\"/-\"><button>-</button></a></td>\n" +
                "       </tr>\n" +
                "       <tr>\n" +
                "           <td><a href =\"/7\"><button>7</button></a></td>\n" +
                "           <td><a href =\"/8\"><button>8</button></a></td>\n" +
                "           <td><a href =\"/9\"><button>9</button></a></td>\n" +
                "           <td><a href =\"/*\"><button>*</button></a></td>\n" +
                "       </tr>\n" +
                "       <tr>\n" +
                "           <td><a href =\"/0\"><button>0</button></a></td>\n" +
                "           <td><a href =\"/C\"><button>C</button></a></td>\n" +
                "           <td><a href =\"/=\"><button>=</button></a></td>\n" +
                "           <td><a href =\"/div\"><button>/</button></a></td>\n" +
                "       </tr>\n" +
                "   </table>\n" +
                "</body>\n" +
                "</html>";
    }
}