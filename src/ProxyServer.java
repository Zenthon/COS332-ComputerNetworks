import java.io.*;
import java.net.*;


public class ProxyServer {
    public static int client_number = 1;
    public static String userInput = "";
    public  static byte[] request = new byte[1024];
    public static void main(String ...args) throws IOException {
        try {
            String host = "localhost";
            int remotePort = 23;
            int localPort = 55555;

            System.out.println("Starting proxy for " + host + ":" + remotePort + " on port " + localPort);
            runServer(host, remotePort, localPort);
        } catch (Exception e) {
            System.err.println(e);
            System.err.println("Usage: ProxyServer " + "<host><remotePort><localPort>");
        }
    }

    public static void runServer(String host, int remotePort, int localPort) throws IOException {
        ServerSocket serverSocket = new ServerSocket(localPort);

        final byte[] reply = new byte[4096];

        while (true) {
            Socket client = serverSocket.accept();
            final InputStream clientReader = client.getInputStream();
            final OutputStream clientWriter = client.getOutputStream();

            Socket server = new Socket(host, remotePort);
            final InputStream serverReader = server.getInputStream();
            final OutputStream serverWriter = server.getOutputStream();
            PrintWriter printWriter = new PrintWriter(server.getOutputStream(), true);


            new Thread(() -> {
                int bytesRead;
                boolean justStarted = true;
                try {
                    String userInput = "";
                    int count = 0;
                    boolean dontPrint = false;

                    while ((bytesRead = clientReader.read(request)) != -1) {
                        count++;

                        byte[] ss = new byte[bytesRead];
                        System.arraycopy(request, 0, ss, 0, bytesRead);

                        String character = new String(ss);
                        if (character.charAt(0) != '\r' && count >= 7)
                            userInput += character.charAt(0);
                        else if (character.charAt(0) == '\r') {
                            System.out.println(userInput);
                            if (userInput.equals("ps"))
                                dontPrint = true;
                        }

                        if (count <= 6 ) {
                            serverWriter.write(request, 0, bytesRead);
                            serverWriter.flush();
                        } else {
                            if (character.charAt(0) == '\r') {
                                if (dontPrint) {
                                    printWriter.println("");
                                    dontPrint = false;
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
            }

        }
    }
}
