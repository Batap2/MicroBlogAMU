package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    static DataBase db;
    // liste de socketHandler car un client peut se connecter sur plusieurs appareils.
    static Hashtable<String, ArrayList<SocketHandler>> connectedClients = new Hashtable<>();

    public static void main(String args[]) throws IOException {

        if(args.length != 1){
            System.out.println("usage : Server.java <serverId>");
            System.exit(-1);
        }

        int serverId = Integer.parseInt(args[0]);
        int port = 12345;
        db = new DataBase(serverId);

        System.out.println("Server ready");
        ServerSocket ss = new ServerSocket(port);

        ExecutorService executorService = Executors.newWorkStealingPool();

        try{
            while(true){
                SocketHandler socketHandler = new SocketHandler(ss.accept());
                executorService.execute(socketHandler);
            }
        } catch(IOException e){
            System.out.println("IOException");
        }
    }

}
