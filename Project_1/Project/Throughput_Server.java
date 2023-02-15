import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//this will serve as the class to perform all the calculations relating to the second part of project 1,
//all pertaining to throughput
class Throughput_Server {
    private final static int PORT = 26973;

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