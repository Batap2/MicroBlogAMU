import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DbReader {
    private File dbFile;
    private Path dbPath;
    BufferedReader db;

    public DbReader() throws IOException {
        dbFile = new File("msgDB");
        dbFile.createNewFile();
        dbPath = Paths.get("msgDB");
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
        int current = 0;
        StringBuilder id = new StringBuilder();
        StringBuilder author = new StringBuilder();
        StringBuilder msg = new StringBuilder();

        while(line.charAt(current) != ' '){
            id.append(line.charAt(current));
            current++;
        }
        current++;
        while(line.charAt(current) != ' '){
            author.append(line.charAt(current));
            current++;
        }
        current++;
        while(current < line.length()){
            msg.append(line.charAt(current));
            current++;
        }
        return new Message(Integer.parseInt(id.toString()), author.toString(), msg.toString());
    }
}
