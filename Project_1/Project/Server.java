
public class Server {


     public static void main(String[] argz ){
         System.out.println("This is the server application, it will require one command line argument, whether "
             + "to use TCP or UDP.");
         if(argz.length != 1 || !(argz[0].equalsIgnoreCase("-TCP")) || !(argz[0].equalsIgnoreCase("-UDP") )){
             System.out.println("You must enter whether the server will support TCP or UDP");
             System.exit(0);
         }
         if(argz[0].equalsIgnoreCase("-TCP")){

         }
         else{

         }
     

}

}
