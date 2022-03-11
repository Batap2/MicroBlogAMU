import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    static DataBase db;
    // liste de socketHandler car un client peut se connecter sur plusieurs appareils.
    static ConcurrentHashMap<String, ArrayList<SocketHandler>> connectedClients = new Hashtable<>();

    static {
        try {
            db = new DataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws IOException {

        int port = 12345;

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
