import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    static DataBase db;

    static {
        try {
            db = new DataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws IOException {

        int port = 12345;
        System.out.println(db.getMessageById(50));

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
