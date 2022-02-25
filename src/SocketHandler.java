import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class SocketHandler implements Runnable{
    Socket s;

    public SocketHandler(Socket soc){
        s = soc;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            while(true){
                try {
                    String msg = in.readLine();

                    if (msg != null) {

                        msg = ">" + msg + "\n";
                        System.out.println(msg);
                        s.getOutputStream().write(msg.getBytes());
                    } else {
                        System.out.println(">>>>>>>>>> Client déconnecté <<<<<<<<<<");
                        break;
                    }
                } catch (SocketException e){
                    System.out.println(">>>>>>>>>> Client déconnecté <<<<<<<<<<");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
