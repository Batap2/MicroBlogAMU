package server;

import java.io.IOException;

public interface ClientSocketHandler extends Runnable {
    void notif(Message m) throws IOException;
    String getConnectedAuthor();
}
