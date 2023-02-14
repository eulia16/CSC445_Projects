import java.io.*;
import java.net.*;
import java.sql.SQLOutput;
import java.util.Scanner;


//this class will accept 2 commmand line arguments, one to define whether to use TCP or UDP, and the other to
//determine whether you would like to measure the RTT(round trip time), or the latency
public class Client {
    static final int PORT = 26971;
    static String HOST = "pi.cs.oswego.edu";


    public static void main(String[] argz) throws IOException, UnknownHostException, InterruptedException{

        new Client(argz);
    }

    public Client(String[] argz) throws IOException, InterruptedException {
        //ensure 2 command line arguments were passed into the program

        //error checking
        if(argz.length != 2){
            System.out.println("You need to enter the data connection model and what to measure.");
            System.exit(0);
        }
        System.out.println("arg1: " + argz[0] + ", arg2: " + argz[1]);

        if( ((argz[0].compareToIgnoreCase("-TCP") != 0)) && ((argz[0].compareToIgnoreCase("-UDP") != 0)) ||
                (argz[1].compareToIgnoreCase("-RTT") !=0) &&  ((argz[1].compareToIgnoreCase("-Throughput") !=0))){
            System.out.println("You must enter a valid protocol" +
                    "(either TCP or TCP) and a valid measurement system(RTT or Throughput)");
        }

        //get signal bit
        int signalBit = getSignalBit();
        int sizeByte = getByteSize();

        //if TCP
        if(argz[0].compareToIgnoreCase("-TCP") == 0){
            //call TCP functions
            System.out.println("this is TCP");
            //establish connection
            Socket clientConnection = Establish_TCP_Connection();

            //way to send and receive data
            DataOutputStream sendMessage = new DataOutputStream(clientConnection.getOutputStream());
            DataInputStream receiveMessage = new DataInputStream(clientConnection.getInputStream());

            //send message to server
            sendMessage.writeInt(signalBit);
            sendMessage.flush();

            //receive message from server
            System.out.println(receiveMessage.readUTF());


            byte[] bytesToSend = new byte[sizeByte];

            //make all bytes 1, just to give them all a value
            bytesToSend = giveBytesMeaning(bytesToSend);


            //start timer right before sending data

            //grab current system time
            long startTime = System.nanoTime();

            //sleep for 1 second just for shits and gigs
            //Thread.sleep(1000);

            //first we must encode the bytes
            bytesToSend = XOR_Bytes(bytesToSend);

            //write bytes to output stream
            sendMessage.writeInt(bytesToSend.length);
            sendMessage.write(bytesToSend);
            sendMessage.flush();


            //read in message back from server
            byte[] message = readMessageFromServer(receiveMessage);

            System.out.println(message.length);

            //decode bytes
            XOR_Bytes(message);


            //validate they are accurate
            if(validatedBytes(message)){
                System.out.println("Bytes were validated, proceed with calculating RTT");


                //calculate RTT
                //we can grab the RTT from here and then compile some graphs from that
                calculateRTT(startTime);

                //exit
                System.exit(0);
            }
            else{
                System.out.println("The bits were corrupted, message RTT unable to be computed.");
                System.exit(0);
            }

        }

        //if UDP
        else{
            System.out.println("You are inside of UDP");
            //make byte with size defined my user
            byte[] message = new byte[sizeByte];
            //give em meaning
            message = giveBytesMeaning(message);

            //create sockets
            DatagramSocket datagramSocket = new DatagramSocket(PORT);
            InetAddress address = InetAddress.getByName(HOST);

            System.out.println(address);


            //start timer right before encoding
            long startTime = System.nanoTime();

            //encode bytes
            message = XOR_Bytes(message);

            //create packet
            DatagramPacket packetToSend = new DatagramPacket(message, message.length, address, PORT);
            System.out.println("Packet has been created");


            //send message
            datagramSocket.send(packetToSend);
            System.out.println("Packet has been sent");

            //packet to recieve data
            DatagramPacket packetToRecieve = new DatagramPacket(message, message.length);
            message = packetToRecieve.getData();

            System.out.println("packet received from server");

            //decode message
            message = XOR_Bytes(message);

            if(validatedBytes(message)){
                System.out.println("Message was validated");

                //calculate RTT
                calculateRTT(startTime);

                //exit
                System.exit(0);
            }
            else{
                System.out.println("Message was unable to be validated");

            }

        }


    }

    public static double calculateRTT(long startTime) throws InterruptedException {
        //calculates time taken
        Thread.sleep(1_000);
        long timeTaken = (System.nanoTime() - startTime);
        double seconds = timeTaken / 1_000_000_000.0;

        System.out.println("RTT: " + seconds + " seconds.");

        return seconds;

    }

    public static byte[] readMessageFromServer(DataInputStream receiveMessage) throws IOException {
        int length = receiveMessage.readInt();
        byte[] message;
        if(length > 0){
            message = new byte[length];
            receiveMessage.readFully(message, 0, length);
        }
        else {
            message = new byte[1];
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

    public static void printBytes(byte[] message){
        for(int i=0; i < message.length; ++i){
            System.out.println(message[i]);
        }
    }
    public static byte[] giveBytesMeaning(byte[] message){
        for(int i=0; i < message.length; ++i){
            message[i] = 1;
        }
        return message;
    }

    public static int getSignalBit()throws IOException{
        Scanner kbd = new Scanner(System.in);
        System.out.println("Enter 1 if you want to keep talking with the server, or 0 if you want to leave");
        //get desired message size
        int size = kbd.nextInt();
        System.out.println("size: " + size);
        return size;
    }
    public static int getByteSize() throws IOException{
        Scanner kbd = new Scanner(System.in);
        System.out.println("Enter the size of the message you want to send.(8, 32, 512, and 1024)");
        //get desired message size
        int size = kbd.nextInt();
        System.out.println("size: " + size);
        return size;
    }


    public static Socket Establish_TCP_Connection() throws UnknownHostException, IOException{
        Socket clientSocket = new Socket(HOST, PORT);
        return clientSocket;
    }


    //encryption method
    public static long XOR(long x){
        return x ^ 1L;
    }

    //encryption and decrytion method
    public static byte[] XOR_Bytes(byte[] bytes){
        //xor every byte w/ 1
        for(int i=0; i<bytes.length; ++i){
            bytes[i] = (byte) (bytes[i] ^ 1l);
        }
        return bytes;
    }

    @Deprecated
    public static byte[] getByteSizeInput() throws IOException{

        //way to read input from user
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the size of the message you want to send.(8, 32, 512, and 1024)");
        //get desired message size
        int size = userInput.read();
        byte[] message = new byte[size];
        //create value for each Byte
        for(int i=0; i < message.length; ++i){
            message[i] = 1;
        }

        //message = XOR_Byte(message);
        int counter=0;

        for(byte b: message){
            System.out.println("byte: " + b);
            System.out.println(counter++);
        }
        return message;

    }


}