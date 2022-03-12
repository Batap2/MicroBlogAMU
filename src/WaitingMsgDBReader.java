import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class WaitingMsgDBReader {
    private File dbFile;
    private Path dbPath;
    private String line;
    private BufferedReader db;

    public WaitingMsgDBReader() throws IOException {
        dbFile = new File("waitingMsgDB");
        dbFile.createNewFile();
        dbPath = Paths.get("waitingMsgDB");
        db = Files.newBufferedReader(dbPath);
    }

    public String nextWaitingClient() throws IOException {
        line = db.readLine();
        if(line == null){
            return null;
        }

        int i = line.indexOf(' ');
        return line.substring(0, i);
    }

    public String getLine(){
        return line;
    }

    public ArrayList<Integer> getIds(){
        ArrayList<Integer> ids = new ArrayList<>();
        if(line == null){
            return null;
        }

        int i = line.indexOf(' ') + 1;
        while (i < line.length()){
            StringBuilder id = new StringBuilder();
            while(i < line.length() && line.charAt(i) != ' '){
                id.append(line.charAt(i));
                i++;
            }
            i++;
            ids.add(Integer.parseInt(id.toString()));
        }
        return ids;
    }
}
