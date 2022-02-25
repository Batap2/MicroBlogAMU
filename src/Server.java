import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String args[]) throws IOException {

        int port = 0;
        if(args.length != 2){
            System.out.println("usage : java MultiThreadServer <port>");
            System.exit(-1);
        } else {
            if (args[0].equals("-t")){
                nbThread = Integer.parseInt(args[1]);
                port = Integer.parseInt(args[2]);
            } else {
                System.out.println("usage : java MultiThreadServer [-t nbThread] <port>");
                System.exit(-1);
            }
        }

        System.out.println("père : " + ProcessHandle.current().pid());
        ServerSocket ss = new ServerSocket(port);
        ExecutorService executorService = Executors.newWorkStealingPool();

        try{
            while(true){
                SocketHandler socketHandler = new SocketHandler(ss.accept());
                System.out.println("ouf");
                executorService.execute(socketHandler);

                //Thread t = new Thread(socketHandler);
                //t.start();
            }
        } catch(IOException e){
            System.out.println("IOException");
        }
    }
}
