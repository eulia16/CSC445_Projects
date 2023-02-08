import javax.xml.crypto.Data;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    final static int PORT = 25000;

    public static void main(String[] argz){
        System.out.println("This is the server application, it will require one command line argument, whether "
                + "to use TCP or UDP.");
        if(argz.length != 1 || (argz[0].compareToIgnoreCase("-TCP")) != 0 && (argz[0].compareToIgnoreCase("-UDP")) != 0){
            System.out.println("You must enter whether the server will support TCP or UDP");
            System.exit(0);
        }

        System.out.println(argz[0]);

        //if user selects TCP
        if(argz[0].compareToIgnoreCase("-TCP") == 0){
            try {
                byte[] buffer = new byte[8];
                //attempt to bind a socket to a port
                ServerSocket serverSocket = establishSocket();
                //continuously wait for connection to be established
                while(true){
                    //'get' socket from client(accept the connection)
                    System.out.println("Waiting for client to connect...");
                    Socket client = serverSocket.accept();
                    System.out.println("Client Connected!");
                    DataOutputStream sendMessage = new DataOutputStream(client.getOutputStream());
                    DataInputStream receiveMessage = new DataInputStream(client.getInputStream());


                    //read all bytes sent from the client
                    int signalBit = receiveMessage.readInt();//readByte();

                    switch(signalBit) {
                        case 1:
                            //confirm revieved bit
                            System.out.println("the signal bit was 1, continue to perform operations: " + signalBit);
                            //send confirmation message
                            sendMessage.writeUTF("We received your message, thank you.");
                            sendMessage.flush();
                            String receivedBytes = receiveMessage.readUTF();  //readAllBytes();
                            //for(byte i : recievedBytes){
                            System.out.println(receivedBytes);
                            //}


                            //if(validatedBytes(receivedBytes)) {
                            sendMessage.writeUTF("This is a test response");//write(recievedBytes);
                            sendMessage.flush();
                            System.out.println("Message bytes to client.");
                            //}
                            break;

                        case 0:
                            System.out.println("the signal bit was 0, we will now quit the program: " + signalBit);
                            sendMessage.writeUTF("You have chosen to turn me off, you will regret this...");
                            System.exit(0);
                            break;

                        default:
                            throw new IllegalStateException("Unexpected value: " + signalBit);
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

    //checks to see if every but is 1, if there is a non 1 slot, something was corrupted
    public static boolean validatedBytes(byte[] message){
        for(int i =0; i<message.length; ++i ){
            if(message[i] != 0)
                return false;
        }

        return true;
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
