import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class SocketHandler implements Runnable{

    private enum Request {PUBLISH, RCV_IDS, RCV_MSG, REPLY, REPUBLISH};
    private Socket socket;
    private String received;
    private PublishRequest publishRequest;

    public SocketHandler(Socket s){
        this.socket = s;
    }

    @Override
    public void run() {
        try {
            while(true){
                try {

                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    received = in.readLine();
                    received = received + "\r\n" + in.readLine() + "\r\n";


                    System.out.println(received);
                    if (received == null) {
                        System.out.println(">>>>>>>>>> Client déconnecté <<<<<<<<<<");
                        break;
                    } else {
                        Request request = recoRequest();
                        requestManager(request);



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

    public Request recoRequest(){
        StringBuilder request = new StringBuilder();

        int current = 0;
        while(current < received.length() && received.charAt(current) != ' '){
            request.append(received.charAt(current));
            current++;
        }

        switch(request.toString()){
            case "PUBLISH":
                return Request.PUBLISH;
            case "RCV_IDS":
                return Request.RCV_IDS;
            case "RCV_MSG":
                return Request.RCV_MSG;
            case "REPLY":
                return Request.REPLY;
            case "REPUBLISH":
                return Request.REPUBLISH;
            default:
                return null;
        }
    }

    public void requestManager(Request req){
        switch (req){
            case PUBLISH:
                publish();
                break;
            default:
                System.out.println("pas de truc");
        }
    }

    public void publish(){
        StringBuilder author = new StringBuilder();
        StringBuilder body = new StringBuilder();

        int current = 0;
        while(current < received.length() && received.charAt(current) != '@'){
            System.out.println("current 1 : " + received.charAt(current));
            current++;
        }
        current++;
        while(received.charAt(current) != '\r'){
            System.out.println("current  2: " + received.charAt(current));
            author.append(received.charAt(current));
            current++;
        }
        current += 2;
        while(received.charAt(current) != '\r'){
            System.out.println("current 3: " + received.charAt(current));
            body.append(received.charAt(current));
            current++;
        }

        publishRequest = new PublishRequest(author.toString(), body.toString());

        System.out.println("Msg ID : " + publishRequest.getIdent());
        System.out.println(publishRequest.getBody());
    }
}
