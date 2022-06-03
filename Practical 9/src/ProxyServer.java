import java.io.*;
import java.net.*;


public class ProxyServer {
    public  static byte[] request = new byte[1024];
    public static void main(String ...args) {
        try {
            String host = "localhost";
            int localPort = 55555;

            System.out.println("Starting proxy for " + host + " on port " + localPort);
            runServer(host, localPort);
        } catch (Exception e) {
            System.err.println(e);
            System.err.println("Usage: ProxyServer " + "<host><remotePort><localPort>");
        }
    }

    public static void runServer(String host, int localPort) throws IOException {
        ServerSocket serverSocket = new ServerSocket(localPort);

        final byte[] reply = new byte[4096];

        while (true) {
            Socket client = serverSocket.accept();

            OutputStream os = client.getOutputStream();
            PrintWriter pw = new PrintWriter(os, true);
            pw.println(print("Please enter the port you want to connect to: "));

            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            int remotePort = Integer.parseInt(br.readLine());

            final InputStream clientReader = client.getInputStream();
            final OutputStream clientWriter = client.getOutputStream();

            Socket server = new Socket(host, remotePort);
            final InputStream serverReader = server.getInputStream();
            final OutputStream serverWriter = server.getOutputStream();
            PrintWriter printWriter = new PrintWriter(server.getOutputStream(), true);


            new Thread(() -> {
                int bytesRead;
                try {
                    String userInput = "";
                    int count = 0;
                    boolean dontPrint = false, stripPrint = false;

                    while ((bytesRead = clientReader.read(request)) != -1) {
                        count++;

                        byte[] ss = new byte[bytesRead];
                        System.arraycopy(request, 0, ss, 0, bytesRead);

                        String character = new String(ss);
                        if (character.charAt(0) != '\r' && count >= 7) userInput += character.charAt(0);
                        else if (character.charAt(0) == '\r') {
                            System.out.println(userInput);
                            if (userInput.equals("ps") || userInput.equals("top") || userInput.equals("4")) dontPrint = true;
                            else if (userInput.contains("& ps") || userInput.contains("& top")) stripPrint = true;
                        }

                        if (count <= 6 ) {
                            serverWriter.write(request, 0, bytesRead);
                            serverWriter.flush();
                        } else {
                            if (character.charAt(0) == '\r') {
                                if (dontPrint) {
                                    printWriter.println("");
                                    dontPrint = false;
                                } else if (stripPrint) {
                                    userInput = userInput.replaceAll("& ps ", "");
                                    userInput = userInput.replaceAll("& ps", "");
                                    userInput = userInput.replaceAll("& top ", "");
                                    userInput = userInput.replaceAll("& top", "");
                                    printWriter.println(userInput);
                                    stripPrint = false;
                                } else {
                                    if (userInput.contains("ishe"))  userInput = "ishe";
                                    printWriter.println(userInput);
                                }
                                userInput = "";
                            }
                        }
                    }
                    serverWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            int bytesRead;
            try {
                while ((bytesRead = serverReader.read(reply)) != -1) {
                    clientWriter.write(reply, 0 , bytesRead);
                    clientWriter.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String print(String text) {
        int line_number = 0;
//        line_number++;
        return "\033[" + line_number + ";0H" + text + "\033[" + 0 + ";" + (text.length() + 2) + "H";
    }
}
