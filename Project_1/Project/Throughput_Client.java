import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

//this class will handle the calculations and operations performed regarding the second half of
//assignment 1, dealing with throughput and its calculations
public class Throughput_Client {

    private final int PORT = 26971;
    private final String HOST = "pi.cs.oswego.edu";

    //public static void main(String[] argz){
    //}

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

            sendMessages(numMessages);

            boolean response = receiveACK();
            //if the response is false, there was either no ACK or something else went wrong
            if(response){
                System.out.println("Successfully transmitted the 1MB of data and received an ACK, way to go");
                clientSocket.close();
            }
            else{
                System.out.println("Something went dreadfully wrong, better luck next time");
                clientSocket.close();
            }



        }
        //else UDP
        else{

        }


    }

    private int getMessageSize(){
        Scanner kbd = new Scanner(System.in);
        System.out.println("Please enter the size of the messages you would like to send(options: 1024, 512, 128)");
        return kbd.nextInt();
    }



}
