import java.net.*;
import java.io.*;
import java.util.*;
//Make new client if the client has an account with us
public class ConnectSMTP implements Runnable{
    private Socket socket;
	/* We need the name of the local machine and remote machine. */
	private String localHost,remoteHost;
	/* Socket input and output Stream */
	DataOutputStream toClient;
	BufferedReader fromClient;

	private static final String CRLF = "\r\n";
	private static final boolean SENDER = false;
	private static final boolean RECEIVER = true;
    public ConnectSMTP(Socket socket) throws Exception 
	{
		this.socket = socket;
	}
    public void run()
	{
		try {
				processRequest();//request to use the mail server 
		}
		catch (Exception e) {
				System.out.println(e);
		}
	}

    private void processRequest(){

		/* Local Variables */
		/* flags to indicate client quit the connection */
		boolean quit=false;
		/* flags to indicate the connection is reset*/
		boolean HELOagain=false;
		/* String variable to store the client command */
		String requestCommand;
		/* String variable to store the sender information */
		String sender="";
		/* String variable to store the receiver information */
		String receiver="";

		try {

			/* Get a reference to the socket's input and output streams. */
			InputStream is = socket.getInputStream();
			toClient = new DataOutputStream(socket.getOutputStream());

			/* Set up input stream filters. */
			InputStreamReader sr = new InputStreamReader(is);
			fromClient = new BufferedReader(sr);

		}catch (IOException e) {
			System.out.println("Initialization error: "+e);
		}
		
		/* SMTP handshake and negotiation. */
		/* We need the name of the local machine and remote machine. */
		try {
			localHost = "DESKTOP-S246F3I";
		}catch (UnknownHostException e) {
			System.out.println("Get hostname error: "+e);
		}
		remoteHost="DESKTOP-S246F3I";

		/* Send the appropriate SMTP Welcome command. */
		reply("House Alarm System Activated. Sensors On!");

		/* Wait the client to send the HELO or EHLO command */
		while (!quit) {
			if ( (requestCommand=fetch()).length()==0 ) quit=true;
			else if ( requestCommand.substring(0,4).equals("HELO") || 
				 requestCommand.substring(0,4).equals("EHLO") ){
				if (parseHELO(requestCommand)) break;
			}

			/* If the client want to quit this session */
			else if (requestCommand.substring(0,4).equals("QUIT")) quit=true;

			/* If the client send the command that is not expected to see now, output an error */
			else if (requestCommand.substring(0,4).equals("MAIL") || 
					 requestCommand.substring(0,4).equals("RCPT") ||
					 requestCommand.substring(0,4).equals("DATA")) {
				reply("Say HELO or QUIT");
			}

			/* If unrecognized command is received, output an error */
			else { reply("Command does not exixt"); }
		}

		while (!quit) {
			HELOagain=false;

			/* Wait for Mail session */
			while (!quit && !HELOagain) {
				if ( (requestCommand=fetch()).length()==0 ) quit=true;

				/* If the client send the appropriate command */
				else if ( requestCommand.substring(0,4).equals("MAIL")) {

					if (validate(requestCommand,SENDER)) {

						/* get the sender address */
						sender="u20469366@tuks.co.za";

						/* tell the client the sender is ok */
						reply("250 OK");
						break;
					}
				}

				/* If the client says HELLO again */
				else if ( requestCommand.substring(0,4).equals("HELO") || 
						  requestCommand.substring(0,4).equals("EHLO") ){
					if (parseHELO(requestCommand)) HELOagain=true;
				}

				/* If the client want to quit this session */
				else if (requestCommand.substring(0,4).equals("QUIT")) quit=true;

				/* If the client send the command that is not expected to see now, output an error */
				else if (requestCommand.substring(0,4).equals("RCPT") ||
						 requestCommand.substring(0,4).equals("DATA")) {
					reply("Wrong Command");
				}
				/* If unrecognized command is received, output an error */
				else { reply("Invalid Command"); }
			}

			/* Wait for Receipant session */
			while (!quit && !HELOagain) {
				if ( (requestCommand=fetch()).length()==0 ) quit=true;

				/* If the client send the appropriate command */
				else if ( requestCommand.substring(0,4).equals("RCPT")) {
					if (validate(requestCommand,RECEIVER)) {

						/* get the receiver address */
						receiver="u20536951@tuks.co.za";

						/* tell the client the receiver is ok */
						reply("250 OK");
						break;
					}
				}

				/* If the client says HELLO again */
				else if ( requestCommand.substring(0,4).equals("HELO") || 
						  requestCommand.substring(0,4).equals("EHLO") ){
					if (parseHELO(requestCommand)) HELOagain=true;
				}

				/* If the client want to quit this session */
				else if (requestCommand.substring(0,4).equals("QUIT")) quit=true;

				/* If the client send the command that is not expected to see now, output an error */
				else if (requestCommand.substring(0,4).equals("DATA") ||
						 requestCommand.substring(0,4).equals("MAIL")) {
					reply("Wrong Command");
				}

				/* If unrecognized command is received, output an error */
				else { reply("Invalid Command"); }
			}

			/* Wait for data session */
			while (!quit && !HELOagain) {
				if ( (requestCommand=fetch()).length()==0 ) quit=true;

				/* If the client send the appropriate command */
				else if ( requestCommand.substring(0,4).equals("DATA")) {
					reply("250 OK");

					/* Call another method to handle this session */
					receiveMessage(sender,receiver);

					/* tell the client the message is saved */
					reply("250 OK");

					HELOagain=true;
				}
				/* If the client says HELLO again */
				else if ( requestCommand.substring(0,4).equals(/* Fill in */) || 
						  requestCommand.substring(0,4).equals(/* Fill in */) ){
					if (parseHELO(requestCommand)) HELOagain=true;
				}

				/* If the client want to quit this session */
				else if (requestCommand.substring(0,4).equals(/* Fill in */)) quit=true;

				/* If the client send the command that is not expected to see now, output an error */
				else if (requestCommand.substring(0,4).equals(/* Fill in */) ||
						 requestCommand.substring(0,4).equals(/* Fill in */)) {
					reply(/* Fill in */);
				}

				/* If unrecognized command is received, output an error */
				else { reply(/* Fill in */); }
			}
		}

		/* tell the client that the server is closing the channel */
		reply(/* Fill in */);
		try {
			socket.close();
		}catch (IOException e) {
			System.out.println("Close connection error: "+e);
		}
	}

	/* This method fetch every line from the client */
	private String fetch(){
		String message="";
		try {
			do {
				message = /* Fill in */;
				if (message == null) break;

				/* If the message length is less than 5 characters */
				if (message.length()</* Fill in */) {

					/* tell the client the command is unrecognized */
					reply(/* Fill in */);
				}
			} while (message.length()</* Fill in */);
		}catch (IOException e) {
			System.out.println("Read socket error: "+e);
		}
		try {
			if (message == null) {
				socket.close();
				return "";
			}
		}catch (IOException e) {
			System.out.println("Close connection error: "+e);
		}
		return message;
		}

	/* This method is to validate the HELLO command */
	private boolean parseHELO (String command) {
		/* flags to indicate the mode of this connection */
		boolean isEHLO;

		/* Check whether it is an EHLO or just an HELO */
		if ( command.substring(0,4).equals(/* Fill in */)) isEHLO=true;
		else isEHLO=false;
		if ( command.substring(4).matches("\\s+\\w+\\s*")) {

			/* If it is an EHLO, display the greetings and server compatibility */
			if (/* Fill in */) {
				reply(/* Fill in */);
				/* Fill in */;
			}

			/* If only HELO is received, just greets the client */
			else { reply(/* Fill in */); }
			return true;
		}

		/* If the HELLO command is not valid */
		else { reply(/* Fill in */);}
		return false;
	}

	/* This method validate the email address */
	private boolean validate(String user,boolean isReceiver) {
		boolean ok=false;

		if (isReceiver) {

			/* The receiver email address is valid */
			if ( user.matches(/* Fill in */) ) {

				/* The receiver email address must have the domain name @cs.ust.hk */
				if (user.matches(/* Fill in */)) ok=true;

				/* If the receiver email address is valid but not in cs domain*/
				else { reply(/* Fill in */); }
			}

			/* If receiver the email addres is not valid, output the error to client */
			else { reply(/* Fill in */); }
		}

		/* The sender email address is valid */
		else if (user.matches(/* Fill in */)) ok=true;

		/* If the sender email address is invalid */
		else { reply(/* Fill in */); }
		return ok;
	}

	private void reply (String command){
		try
		{
			if (!socket.isClosed()) toClient.writeBytes(command+CRLF);			
		}catch (IOException e) {
			System.out.println("Write socket error: "+e);
		}
		System.out.println(command);
		return;
	}

	/* This method process the message body */
	private void receiveMessage(String sender,String receiver){
		String body="";
		String line="";
		MessageSave newMessage;

		try {
			do {
				/* Read each line from client */
				line = /* Fill in */;

				if (line == null) break;

				/* If two dots appear at the beginning of a line, some processing is needed */
				if (line.matches(/* Fill in */)) body+=/* Fill in */;
				else body+=/* Fill in */;

			/* Do it again until the ending delimiter is hit */
			} while(!line.equals(/* Fill in */));
		}catch (IOException e) {
			System.out.println("Read socket error: "+e);
		}
		try	{
			if (line == null) socket.close();
		} catch (IOException e)	{
			System.out.println("Close connection error: "+e);
		}

		/* If the message body is not null, call the MessageSave class to save it */
		if (line != null) newMessage = /* Fill in */;
		return;
	}

    /* Destructor. Closes the connection if something bad happens. */
    protected void finalize() throws Throwable {
	    socket.close();
		super.finalize();
    }
}
