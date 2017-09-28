//Rajat Kachhwaha
//01530088


import java.io.*;
import java.net.*;
import java.util.*;

//My Webserver ( http server)
public class webserver extends Thread {

	static final String HTML_START = "<html>"
			+ "<title>HTTP Server in java</title>" + "<body>";

	static final String HTML_END = "</body>" + "</html>";

	//Global Variables
	
	Socket connectedClient = null;
	BufferedReader inFromClient = null;
	DataOutputStream outToClient = null;
	OutputStream out=null;
    InputStream in=null;
    
	//Constructor
	public webserver(Socket client) {
		connectedClient = client;
	}
	// Implement the run() method for the Runnable interface.
	public void run()
	{
		try {
			processRequest();
		} catch (Exception e) {
			System.out.println(e);
		}

	}
	
	//Here the processing of the request takes place
	public void processRequest() {

		try {

			System.out.println("The Client " + connectedClient.getInetAddress()
					+ ":" + connectedClient.getPort() + " is connected");
			//Reading the request from the client
			inFromClient = new BufferedReader(new InputStreamReader(
					connectedClient.getInputStream()));
			
			//Getting the reference of the output stream
			outToClient = new DataOutputStream(
					connectedClient.getOutputStream());

			//Request String ex : www.google.com 80 GET index.html
			String requestString = inFromClient.readLine();
			String headerLine = requestString;

			//tokenizing w.r.t space
			StringTokenizer tokenizer = new StringTokenizer(headerLine);
			
			//Get or put method
			String httpMethod = tokenizer.nextToken();
			
			//The query String
			String httpQueryString = tokenizer.nextToken();

			StringBuffer responseBuffer = new StringBuffer();
			responseBuffer.append("<b> This is the HTTP Server Home Page.... </b><BR>");
			responseBuffer.append("The HTTP Client request is ....<BR>");

			System.out.println("The HTTP request string is ....");
			while (inFromClient.ready()) {
				// Read the HTTP complete HTTP Query
				responseBuffer.append(requestString + "<BR>");
				System.out.println(requestString);
				requestString = inFromClient.readLine();
			}
//If Get and then we do the code below
			if (httpMethod.equals("GET")) {
				if (httpQueryString.equals("/")) {
					// The default home page
					sendResponse(200, responseBuffer.toString(), false);
				} else {
					// This is interpreted as a file name
					String fileName = httpQueryString.replaceFirst("/", "");
					fileName = URLDecoder.decode(fileName);
					if (new File(fileName).isFile()) {
						sendResponse(200, fileName, true);
					} else {
						sendResponse(404,"<b>The Requested resource not found ...."+ "Usage: http://127.0.0.1:6000 or http://127.0.0.1:6000/</b>",false);
					}
				}
			} 
			
			//if Put then we do the code below
			else if(httpMethod.equals("PUT"))
			{
				callPut(httpQueryString);
			}
			
			else
				sendResponse(404,"<b>The Requested resource not found ...."+ "Usage: http://127.0.0.1:6000 or http://127.0.0.1:6000/</b>",false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Sending the response to the to the client when the method is GET this code will be called

	public void sendResponse(int statusCode, String responseString,
			boolean isFile) throws Exception {

		String statusLine = null;
		String serverdetails = "Server: Java HTTPServer";
		String contentLengthLine = null;
		String fileName = null;
		String contentTypeLine = "Content-Type: text/html" + "\r\n";
		FileInputStream fin = null;

		//Sending the 200 OK or 404 Not found  
		if (statusCode == 200)
			statusLine = "HTTP/1.0 200 OK" + "\r\n";
		else
			statusLine = "HTTP/1.0 404 Not Found" + "\r\n";

		if (isFile) {
			fileName = responseString;
			fin = new FileInputStream(fileName);
			contentLengthLine = "Content-Length: "
					+ Integer.toString(fin.available()) + "\r\n";
			if (!fileName.endsWith(".htm") && !fileName.endsWith(".html"))
				contentTypeLine = "Content-Type: \r\n";
			 } else if (fileName.endsWith(".gif")) {
				 contentTypeLine = "image/gif";
	         } else if (fileName.endsWith(".jpg")) {
	        	 contentTypeLine = "image/jpeg";
	         } else if (fileName.endsWith(".txt")) {
	        	 contentTypeLine = "text/plain";
	         } 

			 else {
			responseString = webserver.HTML_START + responseString
					+ webserver.HTML_END;
			contentLengthLine = "Content-Length: " + responseString.length()
					+ "\r\n";
		}
//Writing to the client
		outToClient.writeBytes(statusLine);
		outToClient.writeBytes(serverdetails);
		outToClient.writeBytes(contentTypeLine);
		outToClient.writeBytes(contentLengthLine);
		outToClient.writeBytes("Connection: close\r\n");
		outToClient.writeBytes("\r\n");

		//Call for the sendfile method
		if (isFile)
			sendFile(fin, outToClient);
		else
			outToClient.writeBytes(responseString);

		outToClient.close();
	}
	
	// Handles  the put
	public void callPut(String urlName) throws IOException 
	{
		String statusLine = null;
		long contentLengthLine = 0;
		String contentTypeLine = "Content-Type: text/html" + "\r\n";
		  
	     try{
	      File temp_file = new File(urlName);
	      String filename = temp_file.getName();
	      
//If file found then we send an oK
	      if(temp_file.exists())
	    	  statusLine = "200 OK";
	      else
	    	  statusLine = "201 Created";
	      
	      PrintWriter fout = null;
	      File file = new File(filename);
			file.createNewFile();
			FileWriter fstream = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fstream);
			System.out.println("File to be uploaded = " + filename);
			fout = new PrintWriter(filename);

			// Upload file here
			
			while(inFromClient.ready()){
				String sCurrentLine = inFromClient.readLine();
				System.out.println(sCurrentLine);
				out.write(sCurrentLine+"\n");
			}		 

			out.close();
			fout.close();    

	     }catch(Exception ex){
		   ex.printStackTrace();
		   statusLine = "500 Internal Server Error";

		 }finally {
			 contentTypeLine = "text/html";
	       contentLengthLine = statusLine.length();
	       // Send the response Headers.
	       sendResponse_put(statusLine, contentTypeLine,contentLengthLine);
	       out.close();
		 }
	   }

	//Sending the response when put is success
public void sendResponse_put(String statusLine,String contentTypeLine,long contentLengthLine) throws IOException 
{
	out = connectedClient.getOutputStream();
	out.write(("HTTP/1.0 " + statusLine + "\r\n").getBytes());
	out.write(("Server: Httpd\r\n").getBytes());
	out.write(("Date: " + new Date() + "\r\n").getBytes());
	out.write(("Content-Type: " + contentTypeLine + "\r\n").getBytes());
	out.write(("Content-Length: " + contentLengthLine + "\r\n").getBytes());
	out.write(("\r\n").getBytes());
}


//Sending the requested file
	public void sendFile(FileInputStream fin, DataOutputStream out)
			throws Exception {
		byte[] buffer = new byte[1024];
		int bytesRead;

		while ((bytesRead = fin.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
		}
		fin.close();
	}

	//Main part of the program
	public static void main(String args[]) throws Exception {

		ServerSocket Server = new ServerSocket(6000, 10,InetAddress.getByName("127.0.0.1"));
		System.out.println("HTTP server Waiting for client on port 6000");
//Creates an new thread as and when the it accepts the new client
		while (true) {
			Socket connected = Server.accept();
			(new webserver(connected)).start();
		}
	}
}
