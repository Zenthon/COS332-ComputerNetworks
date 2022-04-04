//  Isheanesu Joseph Dzingirai - u20536951
//  Muziwandile Ndlovu - u20469366

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Stack;
import java.util.StringTokenizer;

public class HTTPClientHandler extends Thread {
    private Socket socket;
    private BufferedReader clientReader;
    private OutputStream clientWriter;
    private static String answer = "0", response ="", expression ="";
    public enum StatusCode{
        OK("HTTP/1.1 200 OK \r\n"),
        BadRequest("HTTP/1.1 400 Bad Request \r\n"),
        httpVersion("HTTP/1.1 505 HTTP Version Not Supported \r\n"),
        NotImplemented("HTTP/1.1 501 Not Implemented \r\n");
        private String res ; 
        private StatusCode(String response){
            this.res = response;
        }
    }
    public HTTPClientHandler(Socket s) throws IOException {
        socket = s;
        clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        clientWriter = socket.getOutputStream();
    }

    @Override
    public void run() {
        try {
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
            } else {
                if (reqHTML.equals("/"))
                    response =  StatusCode.OK.res  + getHeaders(getCalculator().length()) + getCalculator();
                else if (reqHTML.equals("/DEL")) {
                    StringBuffer sb = new StringBuffer(expression);
                    sb.deleteCharAt(sb.length()-1);
                    expression = answer = sb.toString();
                    response =StatusCode.OK.res + getHeaders(getCalculator().length()) + getCalculator();
                }

                else if (reqHTML.equals("/C")){
                    answer = "0";
                    expression = "";
                    response = StatusCode.OK.res + getHeaders(getCalculator().length()) + getCalculator();
                } else if (reqHTML.equals("/=")) {
                    answer = "" + eval(expression);
                    response = answer.equalsIgnoreCase("INFINITY") ?
                                StatusCode.BadRequest.res + getHeaders(getCalculator().length() + 32) + "<center>Division by zero not allowed. Presss C to continue.<center>\n" + getCalculator() :
                                StatusCode.OK.res + getHeaders(getCalculator().length()) + getCalculator();
                    answer = "Error";

                }
                else {
                    if (Character.isDigit(reqHTML.charAt(1)))
                        expression += reqHTML.charAt(1);
                    else {
                        if (!Character.isDigit(expression.charAt(expression.length()-1))) {
                            StringBuffer sb = new StringBuffer(expression);
                            sb.deleteCharAt(sb.length()-1);
                            expression = sb.toString();
                        }
                        if (!reqHTML.equals("/DIV"))
                            expression += reqHTML.charAt(1);
                        else expression += "/";

                    }
                    answer = expression;
                    response = StatusCode.OK.res + getHeaders(getCalculator().length()) + getCalculator();
                }
                clientWriter.write(response.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                "Connection: keep-alive\r\n" +
                "\r\n";
    }

    public String getCalculator() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "   <title>Calculator</title>\n" +
                "</head>\n" +
                "<body><center>\n" +
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
                "       <tr>\n" +
                "           <td align=\"center\" colspan=\"4\"><a href =\"/DEL\"><button style=\"width:100%\">DEL</button></a></td>\n" +
                "       </tr>\n" +
                "   </table>\n" +
                "</center></body>\n" +
                "</html>";
    }

    public int applyOperand(char operand, int b, int a) {
        switch (operand) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                return (b == 0) ? Integer.MIN_VALUE : (a / b);
        }
        return 0;
    }

    public boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')')
            return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'))
            return false;
        return true;
    }

    public int eval(String expression) {
        char[] tokens = expression.toCharArray();
        Stack<Integer> constants = new Stack<Integer>();
        Stack<Character> operands = new Stack<Character>();

        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i] == ' ')
                continue;
            if (tokens[i] >= '0' && tokens[i] <= '9') {
                StringBuffer sbuf = new StringBuffer();
                while (i < tokens.length && tokens[i] >= '0' && tokens[i] <= '9')
                    sbuf.append(tokens[i++]);
                constants.push(Integer.parseInt(sbuf.toString()));
                i--;
            }
            else if (tokens[i] == '(')
                operands.push(tokens[i]);
            else if (tokens[i] == ')') {
                while (operands.peek() != '(')
                    constants.push(applyOperand(operands.pop(), constants.pop(), constants.pop()));
                operands.pop();
            }

            else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/') {
                while (!operands.empty() && hasPrecedence(tokens[i], operands.peek()))
                    constants.push(applyOperand(operands.pop(), constants.pop(), constants.pop()));
                operands.push(tokens[i]);
            }
        }

        while (!operands.empty())
            constants.push(applyOperand(operands.pop(), constants.pop(), constants.pop()));
        return constants.pop();
    }
}