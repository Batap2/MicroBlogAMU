package client.request;

public class ConnectRequest extends Request {


    public ConnectRequest(String pseudo) {
        super("CONNECT user:@"+pseudo);
    }
}
