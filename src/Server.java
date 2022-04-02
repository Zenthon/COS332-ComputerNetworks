import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.net.*;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class Server {
    public static int client_number = 0;
    public static BufferedReader clientReader = null;
    public static OutputStream clientWriter = null;
    public static String answer = "0";
    public static String expression = "";

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(51515);
        System.out.println("Server running on : " + 51515);

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");

        while (true){
            Socket socket = serverSocket.accept();
            clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clientWriter = socket.getOutputStream();

            String reqLine = clientReader.readLine(),  response = "";
            String[] reqLineArr = reqLine.split(" ");
            if (reqLineArr[0].equals("GET") && reqLineArr[2].equals("HTTP/1.1")) {
               if (reqLineArr[1].equals("/")) {
                   response = getStatusLine(200, "OK") + getHeaders() + "\r\n";
                   clientWriter.write(response.getBytes());
               }
               else {
                   if (reqLineArr[1].equals("/=")) {
                       response = getStatusLine(200, "OK") + getHeaders() + "\r\n";
                       answer = String.valueOf(engine.eval(expression));
                   }
                   else if (reqLineArr[1].equals("/C")){
                       response = getStatusLine(200, "OK") + getHeaders() + "\r\n";
                       answer = expression = "0";
                   }
                   else {
                       response = getStatusLine(200, "OK") + getHeaders() + "\r\n";
                       if (!reqLineArr[1].equals("/div"))
                           expression += reqLineArr[1].charAt(1);
                       else expression += "/";
                       answer = expression;
                   }
                   clientWriter.write(response.getBytes());
               }

               displayCalculator();
            }
        }
    }

    public static String getStatusLine(int statusCode, String reasonPhrase) {
        return "HTTP/1.1 " + statusCode + " " + reasonPhrase + "\r\n";
    }

    public static String getHeaders() {
        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("E, MM dd yyyy HH:mm:ss");
        return "Date: " + currentDate.format(dateFormatter) + " GMT\r\n" + "Server: Muzi and Ishe's Server\r\n" + "Content-Type: text/html\r\n" + "Content-Length: 1000\r\n";
    }

    public static void displayCalculator() throws IOException {
        clientWriter.write("<!DOCTYPE html>".getBytes());
        clientWriter.write("<html lang=\"en\">".getBytes());
        clientWriter.write("<head>".getBytes());
            clientWriter.write("<title>Calculator</title>".getBytes());
        clientWriter.write("</head>".getBytes());
        clientWriter.write("<body>".getBytes());

            clientWriter.write("<table border=\"1\">".getBytes());
                clientWriter.write("<tr>".getBytes());
                    clientWriter.write(("<p><th colspan=\"4\">" + answer + "</th><p>").getBytes());
                clientWriter.write("</tr>".getBytes());
                clientWriter.write("<tr>".getBytes());
                    clientWriter.write("<td><a href =\"/1\"><button>1</button></a></td>".getBytes());
                    clientWriter.write("<td><a href =\"/2\"><button>2</button></a></td>".getBytes());
                    clientWriter.write("<td><a href =\"/3\"><button>3</button></a></td>".getBytes());
                    clientWriter.write("<td><a href =\"/+\"><button>+</button></a></td>".getBytes());
                clientWriter.write("</tr>".getBytes());
                clientWriter.write("<tr>".getBytes());
                    clientWriter.write("<td><a href =\"/4\"><button>4</button></a></td>".getBytes());
                    clientWriter.write("<td><a href =\"/5\"><button>5</button></a></td>".getBytes());
                    clientWriter.write("<td><a href =\"/6\"><button>6</button></a></td>".getBytes());
                    clientWriter.write("<td><a href =\"/-\"><button>-</button></a></td>".getBytes());
                clientWriter.write("</tr>".getBytes());
                clientWriter.write("<tr>".getBytes());
                    clientWriter.write("<td><a href =\"/7\"><button>7</button></a></td>".getBytes());
                    clientWriter.write("<td><a href =\"/8\"><button>8</button></a></td>".getBytes());
                    clientWriter.write("<td><a href =\"/9\"><button>9</button></a></td>".getBytes());
                    clientWriter.write("<td><a href =\"/*\"><button>*</button></a></td>".getBytes());
                clientWriter.write("</tr>".getBytes());
                clientWriter.write("<tr>".getBytes());
                    clientWriter.write("<td><a href =\"/0\"><button>0</button></a></td>".getBytes());
                    clientWriter.write("<td><a href =\"/C\"><button>C</button></a></td>".getBytes());
                    clientWriter.write("<td><a href =\"/=\"><button>=</button></a></td>".getBytes());
                    clientWriter.write("<td><a href =\"div\"><button>/</button></a></td>".getBytes());
                clientWriter.write("</tr>".getBytes());
            clientWriter.write("</table>".getBytes());
        clientWriter.write("</body>".getBytes());
        clientWriter.write("</html>".getBytes());
    }
}