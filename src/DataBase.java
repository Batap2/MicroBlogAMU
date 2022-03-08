import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class DataBase {

    private File dbFile;
    private Path dbPath;

    private int msgNumber = 0;
    private int lastMsgID = 0;

    public DataBase() throws IOException {
        dbFile = new File("msgDB");
        if(dbFile.createNewFile()){
            System.out.println("msgDB created at : " + dbFile.getAbsolutePath());
        } else {
            System.out.println("msgDB founded at : " + dbFile.getAbsolutePath());
        }
        dbPath = Paths.get("msgDB");

        initLastIdAndMsgNumber();
        System.out.println("lastMsgID : " + lastMsgID + ", msgNumber : " + msgNumber);
    }

    public Message writeMsgToDB(String author, String body) throws IOException {
        setLastMsgID(getLastMsgID() + 1);
        setMsgNumber(getMsgNumber() + 1);
        Message msg = new Message(getLastMsgID(), author, body);
        String dbLine = msg.getIdent() + " " + msg.getAuthor() + " " + msg.getBody() + "\n";
        FileWriter fileWriter = new FileWriter(dbFile, true);
        fileWriter.append(dbLine);
        fileWriter.close();
        return msg;
    }

    public Message getMessageById(int id) throws IOException {
        DbReader dbReader = new DbReader();
        if(lastMsgID == 0){
            return null;
        }

        Message msg = dbReader.nextMsg();

        while(msg != null) {
            if(msg.getIdent() == id){
                return msg;
            }
            msg = dbReader.nextMsg();
        }
        return null;
    }

    // params : [author, tag, since_id, limit] null si ignoré
    public Deque<Integer> getIdFromRCV_IDS(String[] params) throws IOException {
        DbReader dbReader = new DbReader();
        Deque<Integer> idList = new LinkedList<>();


        boolean isValid;
        Message msg = dbReader.nextMsg();
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

            msg = dbReader.nextMsg();
        }

        return idList;
    }

    private void initLastIdAndMsgNumber() throws IOException {
        DbReader dbReader = new DbReader();
        int msg_number = 0;

        Message temp = dbReader.nextMsg();
        Message lastMsg = null;
        while(temp != null){
            msg_number++;
            lastMsg = temp;
            temp = dbReader.nextMsg();
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
