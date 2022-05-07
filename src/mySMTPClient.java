/* COMP-430,594 
 * Computer Networks
 * A mail Client
 * Purpose: Sends email
 * Assumption: mailServer:->smtp.bridgew.edu
 */

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class mySMTPClient extends CloseableFrame implements ActionListener
{
  private Button submitBttn,resetBttn;
  private TextField textFrom, textTo, textSubject;
  private TextArea       ta;
  private PrintWriter    pw;
  private BufferedReader br;
  private Socket         sock     = null;

  public static void main(String[] args)
  {
    new  mySMTPClient();
  }

  //constructor
  public  mySMTPClient()
  {
     super("myMailForm");

     Label labelFrom, labelTo,labelSubject ;

     //GridBagLayout is used for myMailForm components
     GridBagConstraints  c;
     GridBagLayout       gridbag;
     gridbag = new GridBagLayout ();
     setLayout(gridbag);

     c = new GridBagConstraints();
     c.insets=new Insets(3, 3, 3, 3);

    //To label
     c.gridx=0;
     c.gridy=1;
     c.anchor=GridBagConstraints.WEST;
     c.gridwidth=2;
     c.gridheight=1;
     labelTo=new Label("TO:");
     gridbag.setConstraints(labelTo, c);
     add(labelTo);

     //TO: text field
     c.gridx=2;
     c.gridy=1;
     c.gridwidth=2;
     c.gridheight=1;
     textTo=new TextField(40);
     gridbag.setConstraints(textTo, c);
     add(textTo);

     //FROM label
     c.gridx=0;
     c.gridy=2;
     c.gridwidth=2;
     c.gridheight=1;
     labelFrom=new Label("FROM:");
     gridbag.setConstraints(labelFrom, c);
     add(labelFrom);

     //FROM:  text field
     c.gridx=2;
     c.gridy=2;
     c.gridwidth=2;
     c.gridheight=1;
     textFrom=new TextField(40);
     gridbag.setConstraints(textFrom, c);
     add(textFrom);

     //SUBJECT: label
     c.gridx=0;
     c.gridy=3;
     c.anchor=GridBagConstraints.WEST;
     c.gridwidth=2;
     c.gridheight=1;
     labelSubject=new Label("Subject");
     gridbag.setConstraints(labelSubject, c);
     add(labelSubject);

     //Subject text field
     c.gridx=2;
     c.gridy=3;
     c.gridwidth=2;
     c.gridheight=1;
     textSubject=new TextField(40);
     gridbag.setConstraints(textSubject, c);
     add(textSubject);

     //message area,
     c.gridx=0;
     c.gridy=4;
     c.gridwidth=5;
     c.gridheight=3;
     c.fill=GridBagConstraints.BOTH;
     ta=new TextArea(13, 40);
     gridbag.setConstraints(ta, c);
     add(ta);
     //Reset button
     c.gridx=0;
     c.gridy=17;
     c.gridwidth=3;
     c.gridheight=1;
     resetBttn=new Button("RESET");
     gridbag.setConstraints(resetBttn, c);
     resetBttn.addActionListener(this);
     add(resetBttn);

     //Send button
     c.gridx=3;
     c.gridy=17;
     c.gridwidth=3;
     c.gridheight=1;
     c.fill = GridBagConstraints.NONE;
     submitBttn=new Button("SEND");
     gridbag.setConstraints(submitBttn, c);
     submitBttn.addActionListener(this);
     add(submitBttn);
     setSize(400,400);
     setVisible(true);
  
  }
  
  //Validate information
  private boolean validFields()
  {
  	boolean validField = true;
  	
  	if (textTo.getText().length() == 0)
  	{
  		validField = false;
  		textTo.setText("<Please enter a recipient>");
  	}
  	if (textFrom.getText().length() == 0)
  	{
  		validField = false;
  		textFrom.setText("<Please enter sender's email>");
  	}

  	return validField;
  }
 //Send an email message via SMTP, adhering to the protocol known as RFC 2821 
  private void sendMessage()
  {
  	int    port = 25;
  	int    code;
  	String hostname = "smtp.bridgew.edu";
  	String from;
  	String to;
  	String subject;

        try
  	{
  	   sock = new Socket(hostname, port);
  	   br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
  	   pw = new PrintWriter(sock.getOutputStream(), true);
  	   
  	   code = getResponseCode(br);
  	   if (code != 220)
  	   {
  	   	  sock.close();
  	   	  throw new Exception("Invalid SMPT server.");
   	   }
   	   
   	   pw.println("HELO "+InetAddress.getLocalHost().getHostName());
   	   code = getResponseCode(br);
   	   if (code != 250)
  	   {
  	   	  sendQuitCommand();
  	   	  throw new Exception("Invalid server.");
   	   }
   	   
   	   pw.println("MAIL FROM: <"+textFrom.getText()+">");
   	   code = getResponseCode(br);
   	   if (code != 250)
  	   {
  	   	  sendQuitCommand();
  	   	  throw new Exception("Invalid FROM address.");
   	   }
   	   
   	   pw.println("RCPT TO: <"+textTo.getText()+">");
   	   code = getResponseCode(br);
   	   if (code != 250)
  	   {
  	   	  sendQuitCommand();
  	   	  throw new Exception("Invalid recipient.");
   	   }
   	   
   	   pw.println("DATA");
   	   code = getResponseCode(br);
   	   if (code != 354)
  	   {
              sendQuitCommand();
  	   	  throw new Exception("Data entry not accepted.");
   	   }
  	   
  	   pw.println("To: "+textTo.getText());
  	   pw.println("From: "+textFrom.getText());
  	   pw.println("Subject: "+textSubject.getText());
  	   pw.println("");
  	   pw.println(ta.getText());
  	   pw.println(".");
           
  	   code = getResponseCode(br);

   	   if (code != 250)
  	   {
  	   	  sendQuitCommand();
  	   	  throw new Exception("Data failed.");
   	   }
         sendQuitCommand();
         System.out.println("Message Sent Successfully!");
           
    }
    catch (Exception e) {e.printStackTrace();}
  }
  
  private void sendQuitCommand() throws Exception
  {
      pw.println("QUIT");
      pw.close();
      br.close();
      sock.close();
  }
  
  //Method gets the response code from the mail server
  private int getResponseCode(BufferedReader br) throws IOException
  {
  	 return new Integer(br.readLine().substring(0, 3)).intValue();
  }
  
  public void actionPerformed(ActionEvent e)
  {
  	if (e.getActionCommand().compareTo("SEND") == 0)
    {
    	if (validFields())
    	{
    		sendMessage();
    	}
	}
	if (e.getActionCommand().compareTo("RESET") == 0)
    {
  	   textTo.setText("");
  	   textFrom.setText("");
  	   textSubject.setText("");
  	   ta.setText("");
	}
  }
 
}

