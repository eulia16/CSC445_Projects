import java.net.SocketException;
import java.net.UnknownHostException;

public class Test_Example_UDP {
         Client_Example_UDP client;

         public static void main(String[] argz) throws SocketException, UnknownHostException{
            Server_Example_UDP server = new Server_Example_UDP();
            Client_Example_UDP client = new Client_Example_UDP();
            server.start();

            String echo = client.sendEcho("hello server");
            System.out.println(echo);
            echo = client.sendEcho("server is working");
            System.out.println(echo);

            client.sendEcho("end");
            client.close();
         }
      
      
      
     }
