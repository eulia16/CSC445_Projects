import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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


     public static void main(String[] argz) throws IOException, UnknownHostException, InterruptedException{
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
             
             //wait to uncomment until the server exists so you can establish some sort of connection
             Socket clientConnection = Establish_TCP_Connection();
             //PrintWriter sendMessage = new PrintWriter(clientConnection.getOutputStream(), true);;
             //BufferedReader readMessage = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
             DataOutputStream sendMessage = new DataOutputStream(clientConnection.getOutputStream());
             BufferedReader receiveMessage = new BufferedReader (new InputStreamReader(clientConnection.getInputStream()));
             

             //we may keep this later, idk yet?
             //byte[] message = getByteSizeInput();
             String message = new String("this is a message to be sent to the server")
             //error checking
             //for(byte b: message){
             //  System.out.println("byte: " + b);
             // }

              //next steps are calling nanotime, sending bytes to server, wait for the bytes to be returned
              //(having the server 'echo' the bytes back(decode using XOR, validate message)), 
              //end nanotime, calculate total RTT it took, then emulate using UDP;
              //grab curreny system time
              long startTime = System.nanoTime();

              Thread.sleep(1000);
             
              //send message to server
              sendMessage.writeByte(1);
              sendMessage.writeBytes( message.toString());
              
              
              long timeTaken = (System.nanoTime() - startTime);
              double seconds = timeTaken / 1_000_000_000.0;

              System.out.println("seconds: " + seconds);
              
          
            }
         //if UDP
         else{
             //call UDP functions
         }


     }

     public static byte[] getByteSizeInput() throws IOException{
         
               //way to read input from user
               BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
               System.out.println("Enter the size of the message you want to send.(8, 32, 512, and 1024)");
               //get desired message size
               int size = userInput.read();
               byte[] message = new byte[size];
               //create value for each Byte
               for(int i=0; i < message.length; ++i){
                message[i] = 1;
               }
               
               message = XOR_Byte(message);
               int counter=0;

               for(byte b: message){
                System.out.println("byte: " + b);
                System.out.println(counter++);
            }
            return message;
     
     }
     
     public static Socket Establish_TCP_Connection() throws UnknownHostException, IOException{
          Socket clientSocket = new Socket(HOST, PORT);
          return clientSocket;
     }

     //encryption method
     public static long XOR(long x){
          return x ^ 1L;
     }

     //encryption method
     public static byte[] XOR_Byte(byte[] bytes){
          //xor every byte w/ 1
          for(int i=0; i<bytes.length; ++i){
               bytes[i] = (byte) (bytes[i] ^ 1);
          }
          return bytes;
     }

}
