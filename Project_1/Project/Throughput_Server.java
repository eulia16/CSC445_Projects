import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

//this will serve as the class to perform all the calculations relating to the second part of project 1,
//all pertaining to throughput
class Throughput_Server {
    private final static int PORT = 26974;

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

                //allow input and output(request/answer)
                byte[] messageReceived = readInBytes(clientSocket);

                System.out.println("Message was received from client, the length is: " + messageReceived.length);

                //send ACK
                sendAcknowledgement(clientSocket);
                System.out.println("Acknowledgment sent!");

                //serverSocket.close();

            }

        }
        //if UDP
        else {
            int tempPacketSize = 1024;
            //we know we are receiving a 1MB packet, so we wi;;
            DatagramSocket datagramSocket = new DatagramSocket(PORT);
            while(true) {
                byte[] overallPacketSizes = new byte[tempPacketSize];
                System.out.println("Waiting to receive message...");
                //create packet to receive the length of the packets that we will be receiving
                DatagramPacket determineSize = new DatagramPacket(overallPacketSizes, overallPacketSizes.length);
                datagramSocket.receive(determineSize);
                System.out.println("Message Received!");
                int lengthOfMessage = determineSize.getLength();
                //System.out.println("Message length is: " + lengthOfMessage);
                //get address of where packet came from
                InetAddress clientAddress = determineSize.getAddress();
                //datagramSocket.connect(clientAddress, PORT);

                byte[] messageSize = new byte[1024 * 1024];//[lengthOfMessage];
                DatagramPacket packetToReceive = new DatagramPacket(messageSize, messageSize.length);

                //creating ACK packet
                String ACKConfirmation = new String("ACK");

                byte[] bytes = ACKConfirmation.getBytes();
                DatagramPacket temp = new DatagramPacket(bytes, bytes.length, clientAddress, PORT);
                datagramSocket.send(temp);
                //System.out.println("Bytes received");


                //we will receive a message telling us how long each packet will be from the client
                System.out.println("number of packets: " + getNumberOfPackets(lengthOfMessage));
                //for (int i = 0; i < 93; ++i){//getNumberOfPackets(lengthOfMessage); ++i) {
                //datagramSocket.

                datagramSocket.receive(packetToReceive);
                System.out.println("Message Received! ");
                //ACKConfirmation = ACKConfirmation.substring(0,3) + i;
                //System.out.println("Creating ACK packet to send...");
                //DatagramPacket packetToSend = new DatagramPacket(ACKConfirmation.getBytes(), ACKConfirmation.length(),
                //clientAddress, PORT);
                //datagramSocket.send(temp);
                //System.out.println("ACK sent!");
                //}
                datagramSocket.send(temp);
                System.out.println("The whole message was received");

            }



        }

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

    private void sendAcknowledgement(Socket clientSocket) throws IOException{
        DataOutputStream sendMessage = new DataOutputStream(clientSocket.getOutputStream());
        String ack = "ACK00001";
        sendMessage.writeInt(ack.length());
        System.out.println("length: " + ack.length());
        sendMessage.writeUTF("ACK00001");
        sendMessage.flush();

        //close output stream
        //sendMessage.close();
        //close socket, were done with them

        //clientSocket.close();


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




}