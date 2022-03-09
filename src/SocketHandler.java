import javax.lang.model.type.IntersectionType;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            case RCV_MSG:
                rcv_msg();
                break;
            case REPLY:
                reply();
                break;
            case REPUBLISH:
                republish();
                break;
            case UNKNOWN:
                response("ERROR");
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
        Deque<Integer> returnedIds = Server.db.getIdFromRCV_IDS(params);
        int n = returnedIds.size();

        if(n == 0){
            response("Aucun message trouvé");
            return;
        }

        for(int i = 0; i < n; i++){
            returnIds.append(returnedIds.poll()).append(' ');
        }
        returnIds.deleteCharAt(returnIds.toString().length() - 1);

        System.out.println("response : " + returnIds.toString());
        response(returnIds.toString());
    }

    public void rcv_msg() throws IOException {
        int i = 0;
        StringBuilder checkCommand = new StringBuilder();
        while(i < received.length() && received.charAt(i) != ':'){
            checkCommand.append(received.charAt(i));
            i++;
        }
        if(!checkCommand.toString().equals("RCV_MSG msg_id")){
            System.out.println("incorrect RCV_MSG request");
            response("ERROR");
            return;
        }
        i++;
        StringBuilder id = new StringBuilder();
        while(i < received.length()){
            id.append(received.charAt(i));
            i++;
        }
        String idStr = id.toString();
        if(idStr.length() == 0){
            System.out.println("incorrect RCV_MSG args");
            response("ERROR");
            return;
        }
        for(int c = 0; c < idStr.length(); c++){
            if(!Character.isDigit(idStr.charAt(c))){
                System.out.println("incorrect RCV_MSG args");
                response("ERROR");
                return;
            }
        }

        Message msg = Server.db.getMessageById(Integer.parseInt(idStr));
        if(msg == null){
            response("Aucun message d'ID " + id);
            return;
        }

        System.out.println("MSG msg_id: " + msg.getIdent() + "\r\n" + msg.getBody());
        response("MSG msg_id: " + msg.getIdent() + "\r\n" + msg.getBody());
    }

    public void reply() throws IOException {
        //TODO finir
        Pattern p = Pattern.compile("^REPLY author:(@\\w+) reply_to_id:(\\d+)\\r\\n(.+)$");
        Matcher m = p.matcher(received);
        if(!m.matches()){
            System.out.println("REPUBLISH syntax error");
            response("ERROR");
            return;
        }

        String author = m.group(1);
        String id = m.group(2);
        String body = m.group(3);

        Message researchedMsg = Server.db.getMessageById(Integer.parseInt(id));
        if(researchedMsg == null){
            System.out.println("Aucun message trouvé pour l'ID " + id);
            response("ERROR");
            return;
        }

        Server.db.writeMsgToDB(author, "["+id+"] "+body);
        response("OK");
    }

    public void republish() throws IOException {
        //TODO finir
        Pattern p = Pattern.compile("^REPUBLISH author:(@\\w+) msg_id:(\\d+)$");
        Matcher m = p.matcher(received);
        if(!m.matches()){
            System.out.println("REPUBLISH syntax error");
            response("ERROR");
            return;
        }

        String author = m.group(1);
        String id = m.group(2);

        Message researchedMsg = Server.db.getMessageById(Integer.parseInt(id));
        if(researchedMsg == null){
            System.out.println("Aucun message trouvé pour l'ID " + id);
            response("ERROR");
            return;
        }

        Server.db.writeMsgToDB(author, researchedMsg.getBody());
        response("OK");
    }

    public void response(String resp) throws IOException {
        resp += "\n";
        socket.getOutputStream().write(resp.getBytes());
    }
}
