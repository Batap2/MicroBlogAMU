package server;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataBase {

    private int serverId;
    private File msgDB;
    private Path msgDBPath;
    private File subscriptionDB;
    private Path subscriptionDBPath;
    private File waitingMsgDB;
    private Path waitingMsgDBPath;
    private File pairsFile;
    private Path pairsPath;

    private int msgNumber = 0;
    private int lastMsgID = 0;

    public DataBase(int serverId) throws IOException {
        this.serverId = serverId;
        msgDB = new File("msgDB"+serverId);
        subscriptionDB = new File("subscriptionDB"+serverId);
        waitingMsgDB = new File("waitingMsgDB"+serverId);
        pairsFile = new File("pairs"+serverId+".cfg");

        if(msgDB.createNewFile()){
            System.out.println("msgDB created at : " + msgDB.getAbsolutePath());
        } else {
            System.out.println("msgDB found at : " + msgDB.getAbsolutePath());
        }
        if(subscriptionDB.createNewFile()){
            System.out.println("subscriptionDB created at : " + subscriptionDB.getAbsolutePath());
        } else {
            System.out.println("subscriptionDB found at : " + subscriptionDB.getAbsolutePath());
        }
        if(waitingMsgDB.createNewFile()){
            System.out.println("waitingMsgDB created at : " + waitingMsgDB.getAbsolutePath());
        } else {
            System.out.println("waitingMsgDB found at : " + waitingMsgDB.getAbsolutePath());
        }
        if(pairsFile.createNewFile()){
            System.out.println("pairs.cfg created at : " + pairsFile.getAbsolutePath());
        } else {
            System.out.println("pairs.cfg found at : " + pairsFile.getAbsolutePath());
        }
        msgDBPath = Paths.get("msgDB"+serverId);
        subscriptionDBPath = Paths.get("subscriptionDB"+serverId);
        waitingMsgDBPath = Paths.get("waitingMsgDB"+serverId);
        pairsPath = Paths.get("pairs"+serverId+".cfg");

        initLastIdAndMsgNumber();
        System.out.println("lastMsgID : " + lastMsgID + ", msgNumber : " + msgNumber);
    }

    public Message writeMsgToDB(String author, String body) throws IOException {
        String enTete = "author:"+author+" msg_id:"+nextID();
        Message msg = new Message(getLastMsgID(), author, body);
        writeMsg(enTete, body);
        return msg;
    }

    public Message writeMsgToDB(String author, String body, int reply_to_id) throws IOException {
        String enTete = "author:"+author+" msg_id:"+nextID()+" reply_to_id:"+reply_to_id;
        Message msg = new Message(getLastMsgID(), author, body, reply_to_id);
        writeMsg(enTete, body);
        return msg;
    }

    public Message writeMsgToDB(String author, String body, boolean isRepublished) throws IOException {
        String enTete = "author:"+author+" msg_id:"+nextID()+" republished:"+isRepublished;
        Message msg = new Message(getLastMsgID(), author, body, isRepublished);
        writeMsg(enTete, body);
        return msg;
    }

    public Message getMessageById(int id) throws IOException {
        MsgDBReader msgDBReader = new MsgDBReader(serverId);
        if(lastMsgID == 0){
            return null;
        }

        Message msg = msgDBReader.nextMsg();

        while(msg != null) {
            if(msg.getIdent() == id){
                return msg;
            }
            msg = msgDBReader.nextMsg();
        }
        return null;
    }

    // params : [author, tag, since_id, limit] null si ignoré
    public Deque<Integer> getIdFromRCV_IDS(String[] params) throws IOException {
        MsgDBReader msgDBReader = new MsgDBReader(serverId);
        Deque<Integer> idList = new LinkedList<>();


        boolean isValid;
        Message msg = msgDBReader.nextMsg();
        while(msg != null){
            isValid = true;
            if(params[0] != null){
                if(!msg.getAuthor().equals(params[0])){
                    isValid = false;
                }
            }
            if(params[1] != null){
                if(!msg.getTagList().contains(params[1])){
                    isValid = false;
                }
            }
            if(params[2] != null){
                if(msg.getIdent() < Integer.parseInt(params[2])){
                    isValid = false;
                }
            }
            if(isValid){
                idList.addFirst(msg.getIdent());
                if(idList.size() > Integer.parseInt(params[3])){
                    idList.removeLast();
                }
            }

            msg = msgDBReader.nextMsg();
        }

        return idList;
    }

    // author = @author, subscription = [@author | #tag]
    public void subscribe(String author, String subscription) throws IOException {
        SubscriptionDBIO sub = new SubscriptionDBIO();
        sub.subscribe(author, subscription);
    }

    public boolean unSubscribe(String author, String subscription) throws IOException {
        SubscriptionDBIO sub = new SubscriptionDBIO();
        sub.unSubscribe(author, subscription);
        return true;
    }

    public void addMsgToWaitingList(int id, String client) throws IOException {
        WaitingMsgDBReader reader = new WaitingMsgDBReader(serverId);
        File tempDBFile = new File("tempWaitingDB"+serverId);
        BufferedWriter writer = Files.newBufferedWriter(Paths.get("tempWaitingDB"+serverId));

        String clientName = reader.nextWaitingClient();
        if(clientName == null){
            String w = client + " " + id + "\n";
            writer.write(w);
            waitingMsgDB.delete();
            tempDBFile.renameTo(waitingMsgDB);
            writer.close();
            return;
        }

        boolean found = false;
        while(clientName != null){
            if(clientName.equals(client)){
                found = true;
                String w = reader.getLine() + " " + id + "\n";
                writer.write(w);
            } else {
                writer.write(reader.getLine() + "\n");
            }
            clientName = reader.nextWaitingClient();
        }

        if(!found){
            writer.write(client + " " + id + "\n");
        }
        waitingMsgDB.delete();
        tempDBFile.renameTo(waitingMsgDB);
        writer.close();
    }

    public ArrayList<Integer> checkWaitingMsg(String client) throws IOException {
        ArrayList<Integer> msgIds = new ArrayList<>();
        WaitingMsgDBReader reader = new WaitingMsgDBReader(serverId);

        String clientName = reader.nextWaitingClient();
        while(clientName != null){
            if(client.equals(clientName)){
                msgIds = reader.getIds();
                deleteClientWaitList(client);
                return msgIds;
            }
            clientName = reader.nextWaitingClient();
        }
        return msgIds;
    }

    private void deleteClientWaitList(String client) throws IOException {
        WaitingMsgDBReader reader = new WaitingMsgDBReader(serverId);
        File tempDBFile = new File("tempWaitingDB2"+serverId);
        BufferedWriter writer = Files.newBufferedWriter(Paths.get("tempWaitingDB2"+serverId));

        String clientName = reader.nextWaitingClient();
        if(clientName == null){
            waitingMsgDB.delete();
            tempDBFile.renameTo(waitingMsgDB);
            writer.close();
            return;
        }

        while(clientName != null){
            if(!clientName.equals(client)){
                writer.write(reader.getLine() + "\n");
            }
            clientName = reader.nextWaitingClient();
        }

        waitingMsgDB.delete();
        tempDBFile.renameTo(waitingMsgDB);
        writer.close();
    }

    private int nextID(){
        setLastMsgID(getLastMsgID() + 1);
        setMsgNumber(getMsgNumber() + 1);
        return getLastMsgID();
    }

    private void writeMsg(String enTete, String body) throws IOException {
        String dbLine = enTete + " | " + body + "\n";
        FileWriter fileWriter = new FileWriter(msgDB, true);
        fileWriter.append(dbLine);
        fileWriter.close();
    }

    private void initLastIdAndMsgNumber() throws IOException {
        MsgDBReader msgDBReader = new MsgDBReader(serverId);
        int msg_number = 0;

        Message temp = msgDBReader.nextMsg();
        Message lastMsg = null;
        while(temp != null){
            msg_number++;
            lastMsg = temp;
            temp = msgDBReader.nextMsg();
        }

        msgNumber = msg_number;
        if(lastMsg == null){
            lastMsgID = 0;
        } else {
            lastMsgID = lastMsg.getIdent();
        }
    }

    public Hashtable<String, InetSocketAddress> getPairs() throws IOException {
        Hashtable<String, InetSocketAddress> table = new Hashtable<>();
        BufferedReader reader = Files.newBufferedReader(pairsPath);

        Pattern p = Pattern.compile("^(\\w+) = (\\w+) (\\w+)$");

        String line = reader.readLine();
        while(line != null){
            Matcher m = p.matcher(line);

            if(m.matches()){
                table.put(m.group(1), new InetSocketAddress(m.group(2), Integer.parseInt(m.group(3))));
            }
            line = reader.readLine();
        }
        return table;
    }

    public int getLastMsgID() {
        return lastMsgID;
    }

    private void setLastMsgID(int lastMsgID) {
        this.lastMsgID = lastMsgID;
    }

    public int getMsgNumber() {
        return msgNumber;
    }

    private void setMsgNumber(int msgNumber) {
        this.msgNumber = msgNumber;
    }
}
