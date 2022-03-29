package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SubscriptionDBIO {
    private File dbFile;
    private Path dbPath;
    String line;
    BufferedReader db;

    public SubscriptionDBIO() throws IOException {
        dbFile = new File("subscriptionDB");
        dbFile.createNewFile();
        dbPath = Paths.get("subscriptionDB");
        db = Files.newBufferedReader(dbPath);
    }

    public Subscription nextSubscription() throws IOException {
        line = db.readLine();
        if(line == null){
            return null;
        }

        int i = line.indexOf(' ');
        String s_on = line.substring(0, i);
        String s_ers = line.substring(i + 1);
        return new Subscription(s_on, s_ers);
    }

    public void subscribe(String author, String subscription) throws IOException {
        File tempDBFile = new File("tempSubDB");
        BufferedWriter writer = Files.newBufferedWriter(Paths.get("tempSubDB"));

        Subscription sub = nextSubscription();
        if(sub == null){
            writer.write(subscription + " " + author + "\n");
            dbFile.delete();
            tempDBFile.renameTo(dbFile);
            writer.close();
            return;
        }
        boolean found = false;
        boolean contains = false;
        while(sub != null){
            String newSubscribersStr = sub.getSubscribersStr();

            if(sub.getSubscription().equals(subscription)){
                found = true;
                String[] subscribers = sub.getSubscribers();
                for(String s : subscribers){
                    if(s.equals(author)){
                        contains = true;
                    }
                }
                if(!contains){
                    newSubscribersStr += " " + author;
                }
            }
            writer.write(sub.getSubscription() + " " + newSubscribersStr + "\n");
            sub = nextSubscription();
        }
        if(!found){
            writer.write(subscription + " " + author + "\n");
        }
        dbFile.delete();
        tempDBFile.renameTo(dbFile);
        writer.close();
    }

    public boolean unSubscribe(String author, String subscription) throws IOException {
        File tempDBFile = new File("tempSubDB");
        BufferedWriter writer = Files.newBufferedWriter(Paths.get("tempSubDB"));

        Subscription sub = nextSubscription();
        if(sub == null){
            dbFile.delete();
            tempDBFile.renameTo(dbFile);
            writer.close();
            return false;
        }

        boolean pass = false;

        while(sub != null){
            String newSubscribersStr = sub.getSubscribersStr();
            String[] newSubs = null;

            if(sub.getSubscription().equals(subscription)){
                String[] subscribers = sub.getSubscribers();
                newSubs = new String[subscribers.length-1];
                int j = 0;
                for(int i = 0; i < subscribers.length; i++){
                    if(subscribers[i].equals(author)){
                        if(subscribers.length == 1){
                            pass = true;
                            continue;
                        }
                        continue;
                    }
                    if(newSubs.length == 0){
                        break;
                    }
                    newSubs[j] = subscribers[i];
                    j++;
                }
            }
            if(pass){
                pass = false;
                sub = nextSubscription();
                continue;
            }

            Subscription newSub;
            if(newSubs == null || newSubs.length == 0){
                newSub = new Subscription(sub.getSubscription(), newSubscribersStr);
            } else {
                newSub = new Subscription(sub.getSubscription(), newSubs);
            }
            writer.write(newSub.getSubscription() + " " + newSub.getSubscribersStr() + "\n");
            sub = nextSubscription();
        }
        dbFile.delete();
        tempDBFile.renameTo(dbFile);
        writer.close();
        return true;
    }
}
