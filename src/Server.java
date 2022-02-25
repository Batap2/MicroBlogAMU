import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String args[]) throws IOException {

        int port = 12345;

        System.out.println("père : " + ProcessHandle.current().pid());
        ServerSocket ss = new ServerSocket(port);
        ExecutorService executorService = Executors.newWorkStealingPool();

        try{
            while(true){
                SocketHandler socketHandler = new SocketHandler(ss.accept());
                System.out.println("ouf");
                executorService.execute(socketHandler);
                
            }
        } catch(IOException e){
            System.out.println("IOException");
        }
    }
}
