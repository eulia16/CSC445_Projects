import java.io.*;
import java.net.*;

public class RTT_Server {
    //port selected by professor, get ports from 26970-26979
    final static int PORT = 26971;

    public static void main(String[] argz) throws IOException {
        new RTT_Server(argz);

    }

    public RTT_Server(String[] argz) throws IOException {
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

                            //read in message from client
                            int length = receiveMessage.readInt();
                            byte[] message;
                            if(length > 0){
                                message = new byte[length];
                                receiveMessage.readFully(message, 0, length);
                            }
                            else {
                                message = new byte[1];
                                System.out.println("An error occurred, turning off the server");
                                System.exit(0);
                            }

                            System.out.println(message.length);

                            //decode the bytes before validating they all = 1
                            decodeEncodeBytes(message);

                            //validate message
                            if(validatedBytes(message)) {
                                //if the message is validated, send back the message

                                //re-encode before sending back to client
                                message = decodeEncodeBytes(message);

                                //sendMessage.writeUTF("Message has been verified");//write(recievedBytes);
                                System.out.println("The message has been verified");
                                //send the bytes back to the client
                                sendMessage.writeInt(message.length);
                                sendMessage.write(message);
                                sendMessage.flush();

                                System.out.println("Message bytes sent to client.");
                            }
                            else{
                                sendMessage.writeUTF("Issue echoing message");
                                serverSocket.close();

                            }

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
            System.out.println("This is UDP");
            DatagramSocket datagramSocket = new DatagramSocket(PORT);
            int messageLength = 1024;

            while(true){
                System.out.println("Waiting to receive packets...");
                //create packet to receive
                byte[] size = new byte[messageLength];
                DatagramPacket messageSize = new DatagramPacket(size, size.length);
                //receive the message
                datagramSocket.receive(messageSize);
                InetAddress clientAddress = messageSize.getAddress();
                System.out.println("packet received from: " + clientAddress.getHostAddress());
                //for testing purposes
                System.out.println("Message was: " + messageSize.getLength() + " bytes.");

                //we must then decode the message, validate it, encode it again, and thgen resend the packet back

                byte[] decodedMessage = decodeEncodeBytes(messageSize.getData());

                //validate message
                if(validatedBytes(decodedMessage)) {
                    //if the message is validated, send back the message

                    //re-encode before sending back to client
                    decodedMessage = decodeEncodeBytes(decodedMessage);

                    //sendMessage.writeUTF("Message has been verified");//write(recievedBytes);
                    System.out.println("The message has been verified");
                    //send the bytes back to the client
                    DatagramPacket packetToSend = new DatagramPacket(decodedMessage, decodedMessage.length, clientAddress, PORT);

                    datagramSocket.send(packetToSend);

                    System.out.println("Message sent to client.");
                }


            }





        }

    }

    public static boolean validatedBytes(byte[] message, int length){
        for(int i =0; i<length; ++i ){
            if(message[i] != 1)
                return false;
        }

        return true;

    }

    //checks to see if every but is 1, if there is a non 1 slot, something was corrupted
    public static boolean validatedBytes(byte[] message){
        for(int i =0; i<message.length; ++i ){
            if(message[i] != 1)
                return false;
        }

        return true;
    }

    public static ServerSocket establishSocket() throws IOException{
        return new ServerSocket(PORT);
    }
    public static byte[] decodeEncodeBytes(byte[] bytes){
        //XOR again to decode the message
        for(int i = 0; i< bytes.length; ++i){
            bytes[i] = (byte) (bytes[i] ^ 1L);
        }
        return bytes;
    }

}