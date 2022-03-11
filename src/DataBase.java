import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Deque;
import java.util.LinkedList;

public class DataBase {

    private File msgDB;
    private Path msgDBPath;
    private File subscriptionDB;
    private Path subscriptionDBPath;

    private int msgNumber = 0;
    private int lastMsgID = 0;

    public DataBase() throws IOException {
        msgDB = new File("msgDB");
        subscriptionDB = new File("subscriptionDB");
        if(msgDB.createNewFile()){
            System.out.println("msgDB created at : " + msgDB.getAbsolutePath());
        } else {
            System.out.println("msgDB founded at : " + msgDB.getAbsolutePath());
        }
        if(subscriptionDB.createNewFile()){
            System.out.println("subscriptionDB created at : " + subscriptionDB.getAbsolutePath());
        } else {
            System.out.println("subscriptionDB founded at : " + subscriptionDB.getAbsolutePath());
        }
        msgDBPath = Paths.get("msgDB");
        subscriptionDBPath = Paths.get("subscriptionDB");

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
        MsgDBReader msgDBReader = new MsgDBReader();
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
        MsgDBReader msgDBReader = new MsgDBReader();
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
        MsgDBReader msgDBReader = new MsgDBReader();
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
