import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLOutput;
import java.util.Scanner;


//this class will accept 2 commmand line arguments, one to define whether to use TCP or UDP, and the other to
//determine whether you would like to measure the RTT(round trip time), or the latency
public class Client {
    static final int PORT = 25000;
    static String HOST = "pi.cs.oswego.edu";


    public static void main(String[] argz) throws IOException, UnknownHostException, InterruptedException{

        Client(argz);
    }

    public static void Client(String[] argz) throws IOException, InterruptedException {
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

        //if TCP
        if(argz[0].compareToIgnoreCase("-TCP") == 0){
            //call TCP functions
            System.out.println("this is TCP");
            //establish connection
            Socket clientConnection = Establish_TCP_Connection();

            //way to send and receive data
            DataOutputStream sendMessage = new DataOutputStream(clientConnection.getOutputStream());
            DataInputStream receiveMessage = new DataInputStream(clientConnection.getInputStream());


            //next steps are calling nanotime, sending bytes to server, wait for the bytes to be returned
            //(having the server 'echo' the bytes back(decode using XOR, validate message)),
            //end nanotime, calculate total RTT it took, then emulate using UDP;

            //get signal bit
            int signalBit = getSignalBit();
            int sizeByte = getByteSize();


            //send message to server
            sendMessage.writeInt(signalBit);// writeByte(size);
            sendMessage.flush();

            //receive message from server
            System.out.println(receiveMessage.readUTF());

            //send bytes

            //***
            // check and see if a long should be used instead if bytes, as a long is 8 bytes long and
            // would be easier/make more sense to XOR real long values with another random long key value
            // ***//
            byte[] bytesToSend = new byte[sizeByte];
            bytesToSend = giveBytesMeaning(bytesToSend);

            //first we must encode the bytes
            bytesToSend = XOR_Bytes(bytesToSend);

            //start timer right before sending data

            //grab current system time
            long startTime = System.nanoTime();

            //sleep for 1 second just for shits and gigs
            //Thread.sleep(1000);

            //write bytes to output stream
            sendMessage.writeInt(bytesToSend.length);
            sendMessage.write(bytesToSend);
            sendMessage.flush();


            String test = receiveMessage.readUTF();
            System.out.println(test);



            //calculates time taken
            Thread.sleep(1000);
            long timeTaken = (System.nanoTime() - startTime);
            double seconds = timeTaken / 1_000_000_000.0;

            System.out.println("RTT: " + seconds);

            //we then can calculate the throughput after this...


        }

        //if UDP
        else{
            //call UDP functions
        }


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

    //encryption method
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