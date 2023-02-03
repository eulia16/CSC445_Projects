import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
     final static int PORT = 20000;

     public static void main(String[] argz){
         System.out.println("This is the server application, it will require one command line argument, whether "
             + "to use TCP or UDP.");
             System.out.println(argz[0]);
             String firstArg = (String) argz[0];
             if(argz.length != 1 || (firstArg.compareToIgnoreCase("-TCP")) != 0 && (firstArg.compareToIgnoreCase("-UDP")) != 0){
             System.out.println("You must enter whether the server will support TCP or UDP");
             System.exit(0);
         }
         System.out.println(argz[0]);
         //if user selects TCP
         if(argz[0].compareToIgnoreCase("-TCP") == 0){
             try {
                
                    byte[] buffer = new byte[1024];
                    //attempt to bind a socket to a port
                    ServerSocket serverSocket = establishSocket();
                    //continuously wait for connection to be established
                    for(;;){
                    //'get' socket from client(accept the connection)
                    Socket client = serverSocket.accept();
                    DataOutputStream sendMessage = new DataOutputStream(client.getOutputStream());
                    //BufferedReader receiveMessage = new BufferedReader( new InputStreamReader(client.getInputStream()));
                    ByteArrayInputStream receiveMessage = new ByteArrayInputStream(buffer);
                    
                    //read all bytes sent from the client
                    byte[] bytes = receiveMessage.readAllBytes();

                    System.out.println("undecoded message");
                    for(int i=0; i< bytes.length; ++i){
                        System.out.println(bytes[i] + "\n" + i);
                    }

                    bytes = decodeBytes(bytes);

                    System.out.println("decoded message");
                    for(int i=0; i< bytes.length; ++i){
                        System.out.println(bytes[i] + "\n" + i);
                    }

                    
                    }

                
             } catch (IOException e) {
                e.printStackTrace();
             }

         }
         //if user selects UDP
         else{

         }
     
}

    public static ServerSocket establishSocket() throws IOException{
        return new ServerSocket(PORT);

}
    public static byte[] decodeBytes(byte[] bytes){
        //XOR again to decode the message
        for(int i = 0; i< bytes.length; ++i){
            bytes[i] = (byte) (bytes[i] ^ 1);
        }
        return bytes;
    }

}
