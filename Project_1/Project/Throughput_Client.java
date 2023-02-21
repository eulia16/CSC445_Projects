import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

//this class will handle the calculations and operations performed regarding the second half of
//assignment 1, dealing with throughput and its calculations
public class Throughput_Client {

    private  int PORT = 26975;
    private  String HOST = "moxie.cs.oswego.edu";

    private final int ACK_SIZE = 8;

    public static void main(String[] argz) throws IOException, InterruptedException {
        new Throughput_Client(argz);
    }

    public Throughput_Client(String[] argz) throws IOException, InterruptedException {
        //simple error checking
        if (argz.length < 1) {
            System.out.println("You need to enter either TCP to UDP as a protocol");
            System.exit(0);
        }
        if (argz[0].compareToIgnoreCase("-TCP") != 0 && argz[0].compareToIgnoreCase("-UDP") != 0) {
            System.out.println("You must enter a correct Protocol");
            System.exit(0);
        }
        //if the user entered an alternative port and hostname, use them
        if(argz.length == 3){
            setHost(argz[1]);
            setPORT(Integer.parseInt(argz[2]));
        }

        int numMessages = getMessageSize();


        if(argz[0].equalsIgnoreCase("-TCP")){
            System.out.println("Creating socket, hold on to your horses");
            Socket clientSocket = new Socket(HOST, PORT);
            System.out.println("Socket successfully created!");

            //begin timer
            long startTime = startTimeTracking();

            //write message indicating the size of the messages

            sendMessages(numMessages, clientSocket);
            System.out.println("message was sent!");


            //end time
            double RTT = calculateRTT(startTime);
            calculateThroughput(RTT);
            System.out.println("Successfully transmitted the 1MB of data and received an ACK, way to go");
            clientSocket.close();


        }
        else{
            DatagramSocket clientSocket = new DatagramSocket(PORT);

            int messageSize = determinePacketSize(numMessages);
            int numberOfMessages = numMessages;

            byte[] packet_bytes = new byte[determinePacketSize(numMessages)];

            //get InetAddress
            InetAddress address = InetAddress.getByName(HOST);

            //send length of messages
            DatagramPacket sendServerPacketLength = new DatagramPacket(packet_bytes, packet_bytes.length, address, PORT);
            clientSocket.send(sendServerPacketLength);

            //packet to send
            byte[] bytes = new byte[messageSize];
            //encode bytes
            bytes = XOR_Bytes(bytes);

            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);

            //ack
            byte[] ackByte = new byte[1];

            DatagramPacket ack = new DatagramPacket(ackByte, ackByte.length);

            //start timer
            long startTime = System.nanoTime();


            for (int j = 0; j < numberOfMessages; ++j) {
                clientSocket.send(packet);

                clientSocket.receive(ack);

                //validate
                if(!validatedBytes(XOR_Bytes(ack.getData()))){
                    System.out.println("Invalid bits, exiting now");
                    System.exit(0);
                }

            }


            clientSocket.receive(ack);

            double RTT = calculateRTT(startTime);
            calculateThroughput(RTT);

       }

   }

    //methods to allow connection to remote servers easier
    public void setHost(String hostName){
        HOST = hostName;
    }

    //methods to allow connection to remote servers easier
    public void setPORT(int port){
        PORT = port;
    }


    private double calculateThroughput(double RTT){
        //the total packet size being sent was 1MB, or 1024 * 1024 bytes, 1_048_576 bytes
        //useful in reading the number of 1 _B per second

        //if bits wanted
        int bit = 8;

        //this returns bytes per second that are transmitted
        double throughout = (1024 * 1024 * bit)  / RTT;
        System.out.println("Throughput: " + throughout + " bits per second");
        return throughout;


    }

    private double calculateRTT(long startTime){
        long timeTaken = (System.nanoTime() - startTime);
        double seconds = timeTaken / 1E9;


        return seconds;
    }

    //encode
    private static byte[] XOR_Bytes(byte[] bytes){
        //xor every byte w/ 1
        for(int i=0; i<bytes.length; ++i){
            bytes[i] = (byte) (bytes[i] ^ 1l);
        }
        return bytes;
    }



    private long startTimeTracking(){
        return System.nanoTime();
    }

    //useful for UDP
    private int determinePacketSize(int numMessages){
        Integer temp = (Integer) numMessages;

        switch(temp){

            case 1024:
                return 1024;

            case 2048:
                return 512;

            case 8192:
                return 128;

            default:
                return -1;
        }
    }

    private boolean receiveACK(Socket clientSocket) throws IOException{
        System.out.println("We are in the receiving ACK now");

        DataInputStream receive = new DataInputStream(clientSocket.getInputStream());

        int lengthOfMessage = receive.readInt();

        System.out.println("This is the value of the return value: " + lengthOfMessage );
        String ack = "";
        if(lengthOfMessage > 0){
            //test
             ack = receive.readUTF();
        }


        //close input stream
        receive.close();
        System.out.println("ack: " + ack);


        //validate the ACK matches what we set it as
        return true;
    }

    private boolean acknowledgeACK(byte[] message){
        return true;
    }



    public static boolean validatedBytes(byte[] message){
        for(int i =0; i<message.length; ++i ){
            if(message[i] != 0)
                return false;
        }

        return true;
    }


    private void sendMessages(int numMessages, Socket clientSocket) throws IOException{
        //get the size of the data to send(for TCP it doesn't matter)

        //create bytes to send
        byte[] bytesToSend = new byte[determinePacketSize(numMessages)];
        //bytes to receive
        byte[] bytesToReceive = new byte[1];

        //open stream
        DataOutputStream sendMessages = new DataOutputStream(clientSocket.getOutputStream());
        DataInputStream receiveMessages = new DataInputStream(clientSocket.getInputStream());
        //write size of data incoming
        sendMessages.writeInt(bytesToSend.length);
        sendMessages.flush();

        //send packets and wait for acks
        for(int i = 0; i < numMessages; ++i){
            //encrypt message
            bytesToReceive = XOR_Bytes(bytesToSend);
            //send message
            sendMessages.write(bytesToSend);
            sendMessages.flush();
            //read encrypted 8 byte ACK
            receiveMessages.read(bytesToReceive);
            //decrypt message
            bytesToReceive = XOR_Bytes(bytesToSend);
            //validate decrypted message
            if(!validatedBytes(bytesToReceive)){
                System.out.println("Bytes invalid, exiting.");
                System.exit(0);
            }


        }
        //receive one last ACK from server
        receiveMessages.read(bytesToReceive);


    }

    private int getMessageSize(){
        Scanner kbd = new Scanner(System.in);
        System.out.println("Please enter the size of the messages you would like to send(options: 1024, 2048, 8192)");
        return kbd.nextInt();
    }

}
