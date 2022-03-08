import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class SocketHandler implements Runnable{

    private enum Request {PUBLISH, RCV_IDS, RCV_MSG, REPLY, REPUBLISH, UNKNOWN};
    private Socket socket;
    private String received;

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
                    while(in.ready()){
                        received = received + "\r\n" + in.readLine() + "\r\n";
                    }
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
                return Request.UNKNOWN;
        }
    }

    public void requestManager(Request req) throws IOException {
        switch (req){
            case PUBLISH:
                publish();
                break;
            case RCV_IDS:
                rcv_ids();
                break;
            case UNKNOWN:
                System.out.println("UNKNOWN");
        }
    }

    public void publish() throws IOException {
        StringBuilder author = new StringBuilder();
        StringBuilder body = new StringBuilder();
        Message message;

        try{
            int current = 0;
            while(received.charAt(current) != '@'){
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

            message = Server.db.writeMsgToDB(author.toString(), body.toString());

            System.out.println("Msg ID : " + message.getIdent());
            System.out.println(message.getBody());

            response("OK");
        } catch (IndexOutOfBoundsException e){
            System.out.println("incorrect PUBLISH request");
            response("ERROR");
        }
    }

    public void rcv_ids() throws IOException {
        String[] params = new String[4]; // [author, tag, since_id, limit] null si ignoré
        params[3] = "5";

        try{
            int current = 0;
            while(current < received.length() && received.charAt(current) != ' '){
                current++;
            }
            current++;

            while(current < received.length()){
                StringBuilder param = new StringBuilder();
                int paramIndex = 0;

                while(received.charAt(current) != ':'){
                    param.append(received.charAt(current));
                    current++;
                }
                current++;

                if(param.toString().equals("author")){
                    paramIndex = 0;
                } else if(param.toString().equals("tag")){
                    paramIndex = 1;
                } else if(param.toString().equals("since_id")){
                    paramIndex = 2;
                } else if(param.toString().equals("limit")){
                    paramIndex = 3;
                } else {
                    System.out.println("incorrect RCV_IDS args");
                    response("ERROR");
                    return;
                }

                StringBuilder value = new StringBuilder();
                while(current < received.length() && received.charAt(current) != ' '){
                    value.append(received.charAt(current));
                    current++;
                }
                current++;

                boolean error = false;
                if(paramIndex == 0){
                    if(value.toString().charAt(0) != '@'){
                        error = true;
                    }
                }
                if(paramIndex == 1){
                    if(value.toString().charAt(0) != '#'){
                        error = true;
                    }
                }
                if(paramIndex == 2 || paramIndex == 3){
                    String valStr = value.toString();
                    if(valStr.length() == 0){
                        error = true;
                    }
                    for(int i = 0; i < valStr.length(); i++){
                        if(!Character.isDigit(valStr.charAt(i))){
                            error = true;
                        }
                    }
                }
                if(error){
                    System.out.println("incorrect RCV_IDS args value");
                    response("ERROR");
                    return;
                }

                params[paramIndex] = value.toString();
            }
        } catch(IndexOutOfBoundsException e){
            System.out.println("incorrect RCV_IDS request");
            response("ERROR");
            return;
        }
        StringBuilder returnIds = new StringBuilder();
        for(Integer id : Server.db.getIdFromRCV_IDS(params)){
            returnIds.append(id).append(' ');
        }
        System.out.println("response : " + returnIds.toString());
        response(returnIds.toString());
    }

    public void response(String resp) throws IOException {
        resp += "\n";
        socket.getOutputStream().write(resp.getBytes());
    }
}
