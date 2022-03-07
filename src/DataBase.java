import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DataBase {

    private File dbFile;
    private Path dbPath;

    private int lastMsgID;

    public DataBase() throws IOException {
        dbFile = new File("msgDB");
        if(dbFile.createNewFile()){
            System.out.println("msgDB created at : " + dbFile.getAbsolutePath());
        } else {
            System.out.println("msgDB founded at : " + dbFile.getAbsolutePath());
        }

        dbPath = Paths.get("msgDB");
        lastMsgID = getLastIDFromDB();
        System.out.println("lastMsgID : " + lastMsgID);

    }

    public void writeMsgToDB(Message msg) throws IOException {
        String dbLine = msg.getIdent() + " " + msg.getAuthor() + " " + msg.getBody() + "\n";
        FileWriter fileWriter = new FileWriter(dbFile, true);
        fileWriter.append(dbLine);
        fileWriter.close();
    }

    public Message getMessageById(int id) throws IOException {
        if(lastMsgID == 0){
            return null;
        }

        BufferedReader db = Files.newBufferedReader(dbPath);
        String line;
        line = db.readLine();

        while(line != null){
            StringBuilder index = new StringBuilder();

            int current = 0;
            while(line.charAt(current) != ' '){
                index.append(line.charAt(current));
                current++;
            }
            int msgId = Integer.parseInt(index.toString());
            if(msgId == id){
                StringBuilder author = new StringBuilder();
                current++;

                while(line.charAt(current) != ' '){
                    author.append(line.charAt(current));
                    current++;
                }
                current++;
                StringBuilder msg = new StringBuilder();
                while(current < line.length()){
                    msg.append(line.charAt(current));
                    current++;
                }
                db.close();
                return new Message(author.toString(), msg.toString());
            }
            line = db.readLine();
        }
        db.close();
        return null;
    }

    public ArrayList<Integer> getIdFromRCV_IDS(String[] params){
        ArrayList<Integer> idList = new ArrayList<>();
        for(String param : params){
            System.out.println(param);
        }
        return idList;
    }

    private int getLastIDFromDB() throws IOException {
        BufferedReader db = Files.newBufferedReader(dbPath);

        String checkLine;
        String lastLine = null;
        checkLine = db.readLine();

        while(checkLine != null){
            lastLine = checkLine;
            checkLine = db.readLine();
        }

        if(lastLine == null){
            return 0;
        }

        StringBuilder index = new StringBuilder();

        int current = 0;
        while(lastLine.charAt(current) != ' '){
            index.append(lastLine.charAt(current));
            current++;
        }
        return Integer.parseInt(index.toString());
    }

    public int getLastMsgID() {
        return lastMsgID;
    }

    public void setLastMsgID(int lastMsgID) {
        this.lastMsgID = lastMsgID;
    }
}
