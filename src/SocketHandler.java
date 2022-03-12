import javax.lang.model.type.IntersectionType;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocketHandler implements Runnable{

    private enum Request {PUBLISH, RCV_IDS, RCV_MSG, REPLY, REPUBLISH, CONNECT, SUBSCRIBE, UNSUBSCRIBE, UNKNOWN};
    private Socket socket;
    private String received;
    private String connectedAuthor = null;

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
                        System.out.println(">>>>>>>>>> Client déconnecté(1) <<<<<<<<<<");
                        if(connectedAuthor != null && Server.connectedClients.containsKey(connectedAuthor)){
                            Server.connectedClients.get(connectedAuthor).remove(this);
                            if(Server.connectedClients.get(connectedAuthor).size() == 0){
                                Server.connectedClients.remove(connectedAuthor);
                            }
                        }
                        break;
                    } else {
                        Request request = recoRequest();
                        requestManager(request);

                    }
                } catch (SocketException e){
                    System.out.println(">>>>>>>>>> Client déconnecté(2) <<<<<<<<<<");
                    if(connectedAuthor != null && Server.connectedClients.containsKey(connectedAuthor)){
                        Server.connectedClients.get(connectedAuthor).remove(this);
                        if(Server.connectedClients.get(connectedAuthor).size() == 0){
                            Server.connectedClients.remove(connectedAuthor);
                        }
                    }
                    printConnected();
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
            case "CONNECT":
                return Request.CONNECT;
            case "SUBSCRIBE":
                return Request.SUBSCRIBE;
            case "UNSUBSCRIBE":
                return Request.UNSUBSCRIBE;
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
            case CONNECT:
                connect();
                break;
            case SUBSCRIBE:
                subscribe();
                break;
            case UNSUBSCRIBE:
                unsubscribe();
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

        Message msg = Server.db.writeMsgToDB(author, body);
        notifAll(msg);

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
            response("ERROR", "Aucun message d'ID " + id);
            return;
        }

        System.out.println("MSG msg_id: " + msg.getIdent() + "\r\n" + msg.getBody());
        response("MSG",msg.getEnTete() + "\n" +msg.getBody());
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

    public void connect() throws IOException {
        Pattern p = Pattern.compile("^CONNECT author:(@\\w+)$");
        Matcher m = p.matcher(received);
        if(!m.matches()){
            System.out.println("CONNECT syntax error");
            response("ERROR", "CONNECT syntax error");
            return;
        }

        if(connectedAuthor != null){
            System.out.println("CONNECT Already connected");
            response("ERROR", "Already connected");
            return;
        }
        connectedAuthor = m.group(1);
        if(Server.connectedClients.containsKey(connectedAuthor)){
            Server.connectedClients.get(connectedAuthor).add(this);
        } else {
            ArrayList<SocketHandler> clientSocketList = new ArrayList<>();
            clientSocketList.add(this);
            Server.connectedClients.put(connectedAuthor, clientSocketList);
        }

        response("OK");
        Server.db.checkWaitingMsg(connectedAuthor);

        printConnected();
    }

    public void subscribe() throws IOException {
        if(connectedAuthor == null){
            System.out.println("SUBSCRIBE connect error");
            response("ERROR", "You must be connected");
            return;
        }
        Pattern p_a = Pattern.compile("^SUBSCRIBE author:(@\\w+)$");
        Pattern p_t = Pattern.compile("^SUBSCRIBE tag:(#\\w+)$");
        Matcher m = p_a.matcher(received);

        if(!m.matches()){
            m = p_t.matcher(received);
            if(!m.matches()){
                System.out.println("SUBSCRIBE syntax error");
                response("ERROR", "SUBSCRIBE syntax error");
                return;
            }
        }

        Server.db.subscribe(connectedAuthor, m.group(1));
        response("OK");
    }

    public void unsubscribe() throws IOException {
        if(connectedAuthor == null){
            System.out.println("UNSUBSCRIBE connect error");
            response("ERROR", "You must be connected");
            return;
        }
        Pattern p_a = Pattern.compile("^UNSUBSCRIBE author:(@\\w+)$");
        Pattern p_t = Pattern.compile("^UNSUBSCRIBE tag:(#\\w+)$");
        Matcher m = p_a.matcher(received);

        if(!m.matches()){
            m = p_t.matcher(received);
            if(!m.matches()){
                System.out.println("UNSUBSCRIBE syntax error");
                response("ERROR", "UNSUBSCRIBE syntax error");
                return;
            }
        }

        Server.db.unSubscribe(connectedAuthor, m.group(1));
        response("OK");
    }

    private void notifAll(Message msg) throws IOException {
        ArrayList<String> researched = new ArrayList<>();
        ArrayList<String> clients = new ArrayList<>();
        ArrayList<String> diconnectedClients = new ArrayList<>();

        researched.add(msg.getAuthor());
        researched.addAll(msg.getTagList());

        SubscriptionDBIO sub = new SubscriptionDBIO();

        Subscription subscription = sub.nextSubscription();
        while(subscription != null){
            if(researched.contains(subscription.getSubscription())){
                String[] subs = subscription.getSubscribers();

                for(String s : subs){
                    if(!clients.contains(s)){
                        clients.add(s);
                    }
                }
            }
            subscription = sub.nextSubscription();
        }
        for(String client : clients){
            if(Server.connectedClients.containsKey(client)){
                ArrayList<SocketHandler> instances = Server.connectedClients.get(client);
                for(SocketHandler instance : instances){
                    instance.notif(msg);
                }
            } else {
                diconnectedClients.add(client);
            }
        }

        for(String client : diconnectedClients){
            Server.db.addMsgToWaitingList(msg.getIdent(), client);
        }
    }

    public void notif(Message msg) throws IOException {
        response("MSG",msg.getEnTete() + "\n" +msg.getBody());
    }

    public void response(String enTete, String body) throws IOException {
        String resp = enTete + "\r\n" + body + "\n";
        socket.getOutputStream().write(resp.getBytes());
    }
    public void response(String enTete) throws IOException {
        String resp = enTete + "\r\n";
        socket.getOutputStream().write(resp.getBytes());
    }

    public String getConnectedAuthor(){
        return connectedAuthor;
    }

    public void printConnected(){
        System.out.println("---------Connected---------");
        for(ArrayList<SocketHandler> l : Server.connectedClients.values()){
            System.out.print("[ ");
            for(int i = 0; i < l.size(); i++){
                System.out.print(l.get(i).getConnectedAuthor()+" ");
            }
            System.out.println("]");
        }
        System.out.println("---------------------------");
    }
}
