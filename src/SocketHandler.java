import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class SocketHandler implements Runnable{
    private Socket socket;
    private enum Request {PUBLISH, RCV_IDS, RCV_MSG, REPLY, REPUBLISH};

    public SocketHandler(Socket s){
        this.socket = s;
    }

    @Override
    public void run() {
        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(true){
                try {
                    String receivedInfo = in.readLine();

                    if (receivedInfo == null) {
                        System.out.println(">>>>>>>>>> Client déconnecté <<<<<<<<<<");
                        break;
                    } else {
                        recoRequest(receivedInfo);

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

    public Request recoRequest(String receivedInfo){
        String request = receivedInfo.
    }
}
