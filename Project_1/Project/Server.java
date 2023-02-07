import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    final static int PORT = 20000;

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
                            System.out.println("the signal bit was 1, continue to perform operations: " + signalBit);
                            sendMessage.writeUTF("We recieved your message, thank you.");
                            break;

                        case 0:
                            System.out.println("the signal bit was 0, we will now quit the program: " + signalBit);
                            sendMessage.writeUTF("You have chosen to turn me off, you will regret this...");
                            System.exit(0);
                            break;

                        default:
                            throw new IllegalStateException("Unexpected value: " + signalBit);
                    }





//                    System.out.println("undecoded message");
//                    for(int i=0; i<bytes.length; ++i){
//                        System.out.print(bytes[i] + "\n" + i);
//                    }



                    //bytes = decodeBytes(bytes);

//                    System.out.println("decoded message");
//                    for(int i=0; i< bytes.length; ++i){
//                        System.out.println(bytes[i] + "\n" + i);
//                    }

                    //if(client.)

                }

                //serverSocket.close();


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