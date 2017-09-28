//Rajat kachhwaha
//01530088

import java.io.*;
import java.net.*;
//class webclient
public class webclient {

	private String host;
	private int port;
	private String action;
	private String fileName;
	private InputStream from_server;
	private OutputStream to_server;
	private Socket socket = null;

	//Constructor
	webclient(String host, int port, String action, String fileName){
		 this.host = host;
		 this.port = port;
		 this.action = action;
		 this.fileName = fileName;
		 doAction();
	 }
	
	private void doAction(){

	try {

   // Open a network socket connection to the specified host and port
   socket = new Socket(host, port);

   // Get input and output streams for the socket
   from_server = socket.getInputStream();
   to_server = socket.getOutputStream();

	  if(action.equalsIgnoreCase("get"))
	    doGetMethod();
	  else if (action.equalsIgnoreCase("put"))
	    doPutMethod();
	  
   socket.close();

 } catch (Exception e) {
	  e.printStackTrace();
 }
}

/**
 * Sends the GET Request to the Server and prints the response.
 */
 private void doGetMethod() throws Exception{  
	   String theLine;

	   try {

	 String req = action + " " + fileName + " "+ "HTTP/1.0" + "\n\n";
	 //Sending the request to the server
     to_server.write(req.getBytes()); 
//Reading  the data into a buffer form the server
  BufferedReader br=new BufferedReader(new InputStreamReader(from_server));

   while ((theLine = br.readLine()) != null) {
      System.out.println(theLine);
   }

}catch(Exception e){
	    e.printStackTrace();
} finally {
		from_server.close();
		to_server.close();
   
}

}


/**
 * Sends the PUT Request to the Server and prints the response.
 */
 private void doPutMethod() throws Exception {

   FileInputStream  file = null;
   byte[] data = new byte[1024];
   int    inCount = 0;
   String theLine;

   try {

       // Send the HTTP PUT command to the Web server.
       String req = action + " " + fileName + " "+ "HTTP/1.0" + "\n\n";
       //Sending the request to the server
       to_server.write(req.getBytes());
      // Open the file.
      file = new FileInputStream(fileName);
      // Read and send file data.

      while (inCount != -1) {
        inCount = file.read(data);

        if (inCount == -1) {
            break;
        }
        //Writing the data into the server
        to_server.write(data, 0, inCount);
      }

    // End of stream.
    socket.shutdownOutput();

    BufferedReader br=new BufferedReader(new InputStreamReader(from_server));

     while ((theLine = br.readLine()) != null) {
        System.out.println(theLine);
     }

  }catch(Exception e){
	    e.printStackTrace();
  } finally {
		from_server.close();
		to_server.close();
     file.close();
  }
}

/**
* Main program.
*/
public static void main(String[] args) {

  // Check the arguments
   if (args.length != 4) {
     System.out.println("HttpClient server port_number GET/PUT filename");
     System.exit(0);
	  }

   String host = args[0];
   int port = Integer.parseInt(args[1]);
   String action = args[2];
   String fileName = args[3];

   webclient client = new webclient(host, port, action, fileName);
}

}
