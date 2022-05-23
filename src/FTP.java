import java.io.*;
import java.net.*;

class FTP {
    private int port = 21;
    private InetAddress localhost;
    private String username = "muzi";
    private String password = "1234";

    public FTP(String host) throws UnknownHostException {
        localhost = InetAddress.getLocalHost();
    }

    public boolean upload(String fileName) throws IOException {
        Socket socket = new Socket(localhost, port);
        BufferedReader clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter clientWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

        System.out.println("Server response: " + clientReader.readLine());
        System.out.println("USER " + username);
        clientWriter.println("USER " + username);
        System.out.println("Server response: " + clientReader.readLine());

        System.out.println("PASS " + password);
        clientWriter.println("PASS " + password);
        System.out.println("Server response: " + clientReader.readLine());

        System.out.println("PWD");
        clientWriter.println("PWD");
        System.out.println("Server response: " + clientReader.readLine());

        System.out.println("PASV");
        clientWriter.println("PASV");
        String server_response = clientReader.readLine();
        System.out.println("Server response: " + server_response);

        String[] ipPortArr = (server_response.substring(server_response.indexOf("(") + 1, server_response.indexOf(")"))).split(",");
        System.out.println("P1: " + ipPortArr[4] + " " + "P2: " + ipPortArr[5]);
        int p1 = Integer.parseInt(ipPortArr[4]);
        int p2 = Integer.parseInt(ipPortArr[5]);

        int secondPort = p1 * 256 + p2;
        System.out.println(secondPort);

        System.out.println("STOR " + fileName);
        clientWriter.println("STOR " + fileName);

        transfer(localhost, secondPort, fileName);
        socket.close();
        return true;
    }

    public boolean transfer(InetAddress localhost, int FTPPort, String fileName) throws IOException {
        System.out.println("Preparing to transfer file...");
        Socket socket = new Socket(localhost, FTPPort);
        PrintWriter clientWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

        System.out.println("Converting html to plain text..");
        StringBuilder content = new StringBuilder();
        BufferedReader in = new BufferedReader(new FileReader(fileName));
        String str;
        while ((str = in.readLine()) != null) content.append(str);
        in.close();

        System.out.println("Transferring file..");
        clientWriter.println(content);

        System.out.println("Transfer complete!");
        socket.close();
        return true;
    }
}