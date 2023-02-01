import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server_Example_UDP  extends Thread {
     
     private DatagramSocket socket;
     private boolean running;
     private byte[] buf = new byte[256];
 
     public Server_Example_UDP() {
         socket = new DatagramSocket(4445);
     }
 
     public void run(){
         running = true;
 
         while (running) {
             DatagramPacket packet = new DatagramPacket(buf, buf.length);

               try{
               socket.receive(packet);
               }
               catch(IOException e){
                    System.out.println(e);
               }
             InetAddress address = packet.getAddress();
             int port = packet.getPort();
             packet = new DatagramPacket(buf, buf.length, address, port);
             String received = new String(packet.getData(), 0, packet.getLength());
             
             if (received.equals("end")) {
                 running = false;
                 continue;
             }
             try {
               socket.send(packet);
          } catch (IOException e) {
               e.printStackTrace();
          }
         }
         socket.close();
     }

}
