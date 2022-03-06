import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class SocketHandler implements Runnable{

    private enum Request {PUBLISH, RCV_IDS, RCV_MSG, REPLY, REPUBLISH};
    private Socket socket;
    private String received;
    private Message message;

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
                    System.out.println("-------------\n" + received + "\n-------------");

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

    public void requestManager(Request req) throws IOException {
        switch (req){
            case PUBLISH:
                publish();
                break;
            default:
                System.out.println("pas de truc");
        }
    }

    public void publish() throws IOException {
        StringBuilder author = new StringBuilder();
        StringBuilder body = new StringBuilder();

        try{
            int current = 0;
            while(current < received.length() && received.charAt(current) != '@'){
                current++;
            }
            while(received.charAt(current) != '\r'){
                author.append(received.charAt(current));
                current++;
            }
            current += 2;
            while(received.charAt(current) != '\r'){
                body.append(received.charAt(current));
                current++;
            }

            message = new Message(author.toString(), body.toString());

            System.out.println("Msg ID : " + message.getIdent());
            System.out.println(message.getBody());

            Server.db.writeMsgToDB(message);
            response("OK");
        } catch (IndexOutOfBoundsException e){
            response("ERROR");
        }
    }

    public void response(String resp) throws IOException {
        resp += "\n";
        socket.getOutputStream().write(resp.getBytes());
    }
}
