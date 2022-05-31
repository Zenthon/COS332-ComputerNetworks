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

            byte [] ss = new byte[1024];
            Thread t = new Thread() {
                public void run() {
                    int bytesRead;
                    try {
                        while ((bytesRead = clientReader.read(request)) != -1) {
                            serverWriter.write(request, 0, bytesRead);
                            serverWriter.flush();
                        }
                        serverWriter.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            t.start();

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
