import java.io.* ;
import java.net.* ;
import java.util.* ;

public final class SMPTServer{
	public static void main(String argv[]) throws Exception {
		int port = 25;
        ServerSocket mailSocket = new ServerSocket(port);
		while (true) {
			Socket SMTPSocket = mailSocket.accept();
			ConnectSMTP connection = new ConnectSMTP(SMTPSocket);
			Thread thread = new Thread(connection);
			thread.start();
		}
	}
}
