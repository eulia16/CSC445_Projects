import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client_Example_UDP {
     private DatagramSocket socket;
     private InetAddress address;
 
     private byte[] buf;
 
     public Client_Example_UDP() throws SocketException, UnknownHostException {
         socket = new DatagramSocket();
         address = InetAddress.getByName("localhost");
     }
 
     public String sendEcho(String msg) {
         buf = msg.getBytes();
         DatagramPacket packet 
           = new DatagramPacket(buf, buf.length, address, 4445);
           try{
           socket.send(packet);
           }
           catch(IOException e){
           System.out.println(e);
           }
           packet = new DatagramPacket(buf, buf.length);
           try{
           socket.receive(packet);
           }
           catch(IOException e){
           System.out.println(e);
           }
           String received = new String(
           packet.getData(), 0, packet.getLength());
           return received;
     }
 
     public void close() {
         socket.close();
     }
    
}

     



