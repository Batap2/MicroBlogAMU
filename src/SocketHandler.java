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
                    System.out.println(received);
                    while(in.ready()){
                        received = received + "\r\n" + in.readLine();
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

        System.out.println("l60");

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
                response("ERROR", "Unknown request");
                System.out.println("UNKNOWN");
        }
    }

    public void publish() throws IOException {
        Pattern p = Pattern.compile("^PUBLISH author:(@\\w+)\\r\\n(.+)$");
        Matcher m = p.matcher(received);
        if(!m.matches()){
            System.out.println("PUBLISH syntax error");
            response("ERROR", "PUBLISH syntax error");
            return;
        }

        String author = m.group(1);
        String body = m.group(2);

        Server.db.writeMsgToDB(author, body);
        response("OK");
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
                    response("ERROR", "RCV_IDS args error");
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
                    response("ERROR", "RCV_IDS args error");
                    return;
                }

                params[paramIndex] = value.toString();
            }
        } catch(IndexOutOfBoundsException e){
            System.out.println("incorrect RCV_IDS request");
            response("ERROR", "RCV_IDS syntax error");
            return;
        }


        StringBuilder returnIds = new StringBuilder();
        Deque<Integer> returnedIds = Server.db.getIdFromRCV_IDS(params);
        int n = returnedIds.size();

        if(n == 0){
            response("ERROR", "No message found");
            return;
        }

        for(int i = 0; i < n; i++){
            returnIds.append(returnedIds.poll()).append(' ');
        }
        returnIds.deleteCharAt(returnIds.toString().length() - 1);

        System.out.println("response : " + returnIds.toString());
        response("MSG_IDS", returnIds.toString());
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
            response("ERROR", "RCV_MSG syntax error");
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
            response("ERROR", "RCV_MSG args error");
            return;
        }
        for(int c = 0; c < idStr.length(); c++){
            if(!Character.isDigit(idStr.charAt(c))){
                System.out.println("incorrect RCV_MSG args");
                response("ERROR", "RCV_MSG args error");
                return;
            }
        }

        Message msg = Server.db.getMessageById(Integer.parseInt(idStr));
        if(msg == null){
            response("Aucun message d'ID " + id);
            return;
        }

        System.out.println("MSG msg_id: " + msg.getIdent() + "\r\n" + msg.getBody());
        response("MSG","MSG msg_id: " + msg);
    }

    public void reply() throws IOException {
        Pattern p = Pattern.compile("^REPLY author:(@\\w+) reply_to_id:(\\d+)\\r\\n(.+)$");
        Matcher m = p.matcher(received);
        if(!m.matches()){
            System.out.println("REPLY syntax error");
            response("ERROR", "REPLY syntax error");
            return;
        }

        String author = m.group(1);
        String id = m.group(2);
        String body = m.group(3);

        Message researchedMsg = Server.db.getMessageById(Integer.parseInt(id));
        if(researchedMsg == null){
            System.out.println("Aucun message trouvé pour l'ID " + id);
            response("ERROR", "No message found");
            return;
        }

        Server.db.writeMsgToDB(author, body, Integer.parseInt(id));
        response("OK");
    }

    public void republish() throws IOException {
        Pattern p = Pattern.compile("^REPUBLISH author:(@\\w+) msg_id:(\\d+)$");
        Matcher m = p.matcher(received);
        if(!m.matches()){
            System.out.println("REPUBLISH syntax error");
            response("ERROR", "REPUBLISH syntax error");
            return;
        }

        String author = m.group(1);
        String id = m.group(2);

        Message researchedMsg = Server.db.getMessageById(Integer.parseInt(id));
        if(researchedMsg == null){
            System.out.println("Aucun message trouvé pour l'ID " + id);
            response("ERROR", "No message found");
            return;
        }

        Server.db.writeMsgToDB(author, researchedMsg.getBody(), true);
        response("OK");
    }

    public void response(String enTete, String body) throws IOException {
        String resp = enTete + "\r\n" + body + "\n";
        socket.getOutputStream().write(resp.getBytes());
    }
    public void response(String enTete) throws IOException {
        String resp = enTete + "\n";
        socket.getOutputStream().write(resp.getBytes());
    }
}
