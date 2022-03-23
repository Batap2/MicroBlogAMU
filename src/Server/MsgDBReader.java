package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MsgDBReader {
    private int serverId;
    private File dbFile;
    private Path dbPath;
    BufferedReader db;

    public MsgDBReader(int serverId) throws IOException {
        this.serverId = serverId;
        dbFile = new File("msgDB"+serverId);
        dbFile.createNewFile();
        dbPath = Paths.get("msgDB"+serverId);
        db = Files.newBufferedReader(dbPath);
    }

    public Message nextMsg() throws IOException {
        String line = db.readLine();
        if(line == null){
            return null;
        }
        return dbLineToMessage(line);
    }

    private Message dbLineToMessage(String line){
        Pattern p = Pattern.compile("^author:(@\\w+) msg_id:(\\d+) \\| (.+)$");
        Pattern p_reply = Pattern.compile("author:(@\\w+) msg_id:(\\d+) reply_to_id:(\\d+) \\| (.+)");
        Pattern p_republish = Pattern.compile("author:(@\\w+) msg_id:(\\d+) republished:\\w+ \\| (.+)");
        Matcher m = p.matcher(line);
        Message msg;

        if(m.matches()){
            return new Message(Integer.parseInt(m.group(2)), m.group(1), m.group(3));
        }
        m = p_reply.matcher(line);
        if(m.matches()){
            return new Message(Integer.parseInt(m.group(2)), m.group(1), m.group(4), Integer.parseInt(m.group(3)));
        }
        m = p_republish.matcher(line);
        if(m.matches()){
            return new Message(Integer.parseInt(m.group(2)), m.group(1), m.group(3), true);
        }
        return null;
    }
}
