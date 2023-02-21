import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

//this will serve as the class to perform all the calculations relating to the second part of project 1,
//all pertaining to throughput
class Throughput_Server {
    private final static int PORT = 26975;

    public static void main(String[] argz) throws IOException{
        new Throughput_Server(argz);
    }
    public Throughput_Server(String[] argz) throws IOException {
        if (argz.length < 1) {
            System.out.println("You need to enter either TCP ro UDP as a protocol");
            System.exit(0);
        }
        if (argz[0].compareToIgnoreCase("-TCP") != 0 && argz[0].compareToIgnoreCase("-UDP") != 0) {
            System.out.println("You must enter a correct Protocol");
            System.exit(0);
        }

        if (argz[0].equalsIgnoreCase("-TCP")) {
            //bind socket tp port
            ServerSocket serverSocket = new ServerSocket(PORT);

            while(true) {
                System.out.println("Awaiting connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client Connected!");

                //read all bytes from socket
                int i = readBytesCorrectly(clientSocket);

                System.out.println("Full message has been received from client");
                //send ACK
                //sendAcknowledgement(clientSocket);

            }


        }


        else{

            byte[] buffer = new byte[1024];
            DatagramSocket datagramSocket = new DatagramSocket(PORT);
            DatagramPacket packetToReceive = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(packetToReceive);



            int messageSize = packetToReceive.getLength();
            int numberOfMessages = getNumberOfPackets(messageSize);
            //byte[] bytes = new byte[1048576];
            byte[] receivedBytes = new byte[messageSize];
            byte[] ackByte = new byte[1];



            //encode ACK
            ackByte = XOR_Bytes(ackByte);

            DatagramPacket receivedPacket = new DatagramPacket(receivedBytes, messageSize);
            DatagramPacket ack = new DatagramPacket(ackByte, ackByte.length, packetToReceive.getAddress(), packetToReceive.getPort());

            for (int i = 0; i < numberOfMessages; ++i) {


                datagramSocket.receive(receivedPacket);

                //validate
                if(!validatedBytes(XOR_Bytes(receivedPacket.getData()))){
                    System.out.println("Bytes corrupted");
                    System.exit(0);
                }

                datagramSocket.send(ack);

            }
            //DatagramPacket ack = new DatagramPacket(ackByte, ackByte.length, address, sendToPort);
            datagramSocket.send(ack);

        }


    }

    //validate bytes
    public static boolean validatedBytes(byte[] message){
        for(int i =0; i<message.length; ++i ){
            if(message[i] != 0)
                return false;
        }

        return true;
    }


    //decode
    private static byte[] XOR_Bytes(byte[] bytes){
        //xor every byte w/ 1
        for(int i=0; i<bytes.length; ++i){
            bytes[i] = (byte) (bytes[i] ^ 1l);
        }
        return bytes;
    }

    private int getNumberOfPackets(int numMessages){
        Integer temp = (Integer) numMessages;

        switch(temp){

            case 1024:
                return 1024;

            case 512:
                return 2048;

            case 128:
                return 8192;

            default:
                return -1;
        }
    }

    private void sendAcknowledgement(Socket clientSocket) throws IOException {
        DataOutputStream sendMessage = new DataOutputStream(clientSocket.getOutputStream());
        String ack = "ACK00001";
        sendMessage.writeInt(ack.length());
        System.out.println("length: " + ack.length());
        sendMessage.writeUTF("ACK00001");
        sendMessage.flush();
    }

    public static void printBytes(byte[] message){
        for(int i=0; i < 20; ++i){
            System.out.println(message[i]);
        }
    }

    //get message from stream
    private byte[] readInBytes(Socket clientSocket) throws IOException{
        byte[] message;
        DataInputStream receive = new DataInputStream(clientSocket.getInputStream());

        int lengthOfMessage = receive.readInt();
        message = new byte[lengthOfMessage];

        if(lengthOfMessage > 0){
            receive.readFully(message);
        }
        //close input stream
        //receive.close();
        return message;
    }

    private int readBytesCorrectly(Socket clientSocket) throws IOException{
        DataInputStream receiveMessages = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream sendMessages = new DataOutputStream(clientSocket.getOutputStream());
        byte[] buffer;

        int packetSize  = receiveMessages.readInt();
        System.out.println("PacketSize: " + packetSize);
        int numMessages = getNumberOfPackets(packetSize);
        buffer = new byte[packetSize];

        byte[] messageToSend = new byte[1];
        //encode
        messageToSend = XOR_Bytes(messageToSend);

        System.out.println("About to receive data");
        //until a packet that has an ending signal in it
        for(int i=0; i<numMessages; ++i){
            //read in encrypted message
            receiveMessages.read(buffer);

            if(!validatedBytes(XOR_Bytes(buffer))){
                System.out.println("Bytes corrupted");
                System.exit(0);
            }

            //write encoded ACK
            sendMessages.write(messageToSend);
            sendMessages.flush();

        }
        sendMessages.write(messageToSend);

        return 1;
    }




}

