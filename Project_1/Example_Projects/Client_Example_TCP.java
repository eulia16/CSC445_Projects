//package Project_1;

import java.net.*;
import java.io.*;
//this will srerve as the client, i.e. this will live on this computer and serve as the client that communicates with
//whatever servers we decide, I am leaning towards using the schools server pi and rho as two servers that this client
//will communicate with, and then I will attempt to use my raspberry pi as a server and have it attached to a seperate
//network, such as colloseum or something of that nature. We can ask Professor Lea at a later date to see if that 
//suffices or if he has any alternative recommendations. 
public class Client_Example_TCP{
     public static void main(String[] args) {
          String host = "pi.cs.oswego.edu";
          int echoServicePortNumber = 5325;
      
          Socket echoSocket = null;
          PrintWriter out = null;
          BufferedReader in = null;
      
          try {
            echoSocket = new Socket(host, echoServicePortNumber);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                                           echoSocket.getInputStream()));
          } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
            e.printStackTrace();
            System.exit(1);
          } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection.");
            e.printStackTrace();
            System.exit(1);
          }
      
          try {
            BufferedReader stdIn = 
              new BufferedReader(new InputStreamReader(System.in));
            String userInput;
      
            while ((userInput = stdIn.readLine()) != null) {
              out.println(userInput);
              System.out.println("echo: " + in.readLine());
            }
            
            out.close();
            in.close();
            stdIn.close();
            echoSocket.close();
          }
          catch (IOException ex) {
            System.err.println("IO failure.");
            ex.printStackTrace();
          }
        }
      }
    /*  

static final int MAX_PORT_NUMBER = 65535;
static final int MIN_PORT_NUMBER = 1024;

//we will take in 2 arguments from the command line, first the host destination name, and secondly, the
//port we want to bind this client socket to     
public static void main(String argz[]){
//establish host and port number to bind socket
String host = "pi.cs.oswego.edu";
int clientPort = 5325;

//variables for connection/reading/writing
Socket client = null;
PrintWriter out = null;
BufferedReader in = null;


//we will place everything in a try w/ resources statement so java automatically closes the socket and any
//active connections
try{
//instantiate new socket
client = new Socket(host, clientPort);
//instantiate variable to send text
out = new PrintWriter(client.getOutputStream(), true);
//instantiate variable to read messages in from the server
in = new BufferedReader(new InputStreamReader(client.getInputStream()));

}
catch(UnknownHostException e){
     e.printStackTrace();
     System.out.println("Somethin fucked up");
     System.exit(1);
}
catch(IOException e){
     e.printStackTrace();
     System.out.println("Somethin fucked up");
     System.exit(1);
}
//if that try catch statement succeeds, we then will attempt to read and write messages to and from the server

try{
   BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
   String userInput;

   while((userInput = stdIn.readLine()) != null){
     out.println(userInput);
     System.out.println("echoed: " + in.readLine());
   }

    out.close();
    in.close();
    stdIn.close();
    client.close();

}
catch(IOException e){
     e.printStackTrace();
     System.out.println("Somethin fucked up");
     System.exit(1);
}





}





/* 
//user input validation
if(argz.length != 2){
     System.out.println("You must enter a hostname and a port number")
}
try{
     int isInt = Integer.parseInt(argz[1])
}
catch(NumberFormatException e){
     System.out.println("Enter an integer for the port number between 1024 and 65536");
     System.exit(1);
}
if(isInt > MAX_PORT_NUMBER || inInt < MIN_PORT_NUMBER ){
     System.out.println("Enter a number between 1024 and 65525(these are the only available ports");
     System.exit(1);
}
*/
