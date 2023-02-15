import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

//this class will handle the calculations and operations performed regarding the second half of
//assignment 1, dealing with throughput and its calculations
public class Throughput_Client {

    private final int PORT = 26973;
    private final String HOST = "pi.cs.oswego.edu";

//    private final int ACK_SIZE = 8;

    public static void main(String[] argz) throws IOException{
        new Throughput_Client(argz);
    }

    public Throughput_Client(String[] argz) throws IOException {
        //simple error checking
        if (argz.length < 1) {
            System.out.println("You need to enter either TCP to UDP as a protocol");
            System.exit(0);
        }
        if (argz[0].compareToIgnoreCase("-TCP") != 0 && argz[0].compareToIgnoreCase("-UDP") != 0) {
            System.out.println("You must enter a correct Protocol");
            System.exit(0);
        }

        //we can grab some user input that is consistent between both UDP and TCP such as
        //the number of messages they want to send to the server(and then we can calculate
        //the subsequent packet sizes(doesn't apply to TCP in regard to packet sizes))
        int numMessages = getMessageSize();


        if(argz[0].equalsIgnoreCase("-TCP")){
            System.out.println("Creating socket, hold on to your horses");
            Socket clientSocket = new Socket(HOST, PORT);
            System.out.println("Socket successfully created!");

            //begin timer
            long startTime = startTimeTracking();

            sendMessages(numMessages, clientSocket);
            System.out.println("message was sent!");

            boolean response = receiveACK(clientSocket);
            //if the response is false, there was either no ACK or something else went wrong
            if(response){
                //end time
                double RTT = calculateRTT(startTime);
                calculateThroughput(RTT);
                System.out.println("Successfully transmitted the 1MB of data and received an ACK, way to go");
                clientSocket.close();
            }
            else{
                //end time
                double RTT =calculateRTT(startTime);
                calculateThroughput(RTT);
                System.out.println("Something went dreadfully wrong, better luck next time");
                clientSocket.close();
            }

        }
        //else UDP
        else{

        }


    }

    private double calculateThroughput(double RTT){
        //the total packet size being sent was 1MB, or 1024 * 1024 bytes, 1_048_576 bytes
        double kilobytes = .001;
        double megabytes = .001 * kilobytes;
        //this returns bytes per second that are transmitted
        double throughout = 1_048_576 * kilobytes / RTT ;
        System.out.println("Throughput: " + throughout + " KiloBytes per second");
        return throughout;


    }

    private double calculateRTT(long startTime){
        long timeTaken = (System.nanoTime() - startTime);
        double seconds = timeTaken / 1_000_000_000.0;

        System.out.println("RTT: " + seconds + " seconds.");

        return seconds;
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
        byte[] message;
        DataInputStream receive = new DataInputStream(clientSocket.getInputStream());

        int lengthOfMessage = receive.readInt();
        message = new byte[lengthOfMessage];
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
        return true;//acknowledgeACK(message);
    }

    private boolean acknowledgeACK(byte[] message){
        return true;
    }

    private static byte[] giveBytesMeaning(byte[] message){
        for(int i=0; i < message.length; ++i){
            message[i] = 1;
        }
        return message;
    }

    public static boolean validatedBytes(byte[] message){
        for(int i =0; i<message.length; ++i ){
            if(message[i] != 1)
                return false;
        }

        return true;
    }


    private void sendMessages(int numMessages, Socket clientSocket) throws IOException{
        //get the size of the data to send(for TCP it doesn't matter)
        int sizeOfData = numMessages * determinePacketSize(numMessages);
        //create bytes to send
        byte[] bytesToSend = new byte[sizeOfData];
        //get size of packet as a result of the number of messages you have chosen to send
        int number = determinePacketSize(numMessages);
        System.out.println("Number from bytes to send: " + number + ", numMessages value:" + numMessages);
        bytesToSend = giveBytesMeaning(bytesToSend);
        //open stream
        DataOutputStream sendMessages = new DataOutputStream(clientSocket.getOutputStream());
        //write size of data incoming
        sendMessages.writeInt(sizeOfData);
        //write 1MB data
        //***See if this is a correct way to send data over TCP***//
        sendMessages.write(bytesToSend);
        sendMessages.flush();

    }

    private int getMessageSize(){
        Scanner kbd = new Scanner(System.in);
        System.out.println("Please enter the size of the messages you would like to send(options: 1024, 2048, 8192)");
        return kbd.nextInt();
    }


//    private double TransferTimeCalculation(int RTT, int bandwidth, int transferSize){
//    //formula: TransferTime = RTT + 1 / Bandwidth * TransferSize
//        return RTT + 1 / bandwidth * transferSize;
//
//
//    }



}
