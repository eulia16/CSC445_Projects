import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


//this class will accept 2 commmand line arguments, one to define whether to use TCP or UDP, and the other to
//determine whether you would like to measure the RTT(round trip time), or the latency
public class Client {
     static final int PORT = 20000;
     static String HOST = "pi.cs.oswego.edu";


     public static void main(String[] argz){
         //ensure 2 command line arguments were passed into the program

         //error checking
         if(argz.length != 2){
          System.out.println("You need to enter the data connection model and what to measure.");
          System.exit(0);
         }
         System.out.println("arg1: " + argz[0] + ", arg2: " + argz[1]);

         //fix this later idk whats wrong w it
         if( !(argz[0].equalsIgnoreCase("TCP")) || !(argz[0].equalsIgnoreCase("UDP")) || 
         !(argz[1].equalsIgnoreCase("RTT")) || !(argz[1].equalsIgnoreCase("Throughput"))){
          System.out.println("You must enter a valid protocol" +
          "(either TCP or TCP) and a valid measurement system(RTT or Throughput)");
         }
         long x = 4;
         long y = XOR(x);
         System.out.println("xor: " + y + ", second xor: " + XOR(y));
         //if TCP
         if(argz[0].equals("TCP")){
             //call TCP functions
             System.out.println("this is TCP");
             //establish connection
             try{
             Socket clientConnection = Establish_TCP_Connection();
             PrintWriter sendMessage = new PrintWriter(clientConnection.getOutputStream(), true);;
             BufferedReader readMessage = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));;
             
          }
            catch (UnknownHostException e) {
                System.err.println("Don't know about host " + HOST);
                e.printStackTrace();
                System.exit(1);
         }  catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection.");
                e.printStackTrace();
                System.exit(1);
        }

         }
         //if UDP
         else{
             //call UDP functions
         }


     }
     
     public static Socket Establish_TCP_Connection() throws UnknownHostException, IOException{
          Socket clientSocket = new Socket(HOST, PORT);
          return clientSocket;
     }

     //encryption method
     public static long XOR(long x){
          return x ^ 1L;
     }

}
