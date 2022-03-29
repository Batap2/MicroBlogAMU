package client.request;

public class RepublishRequest extends Request {

    public RepublishRequest(String user, String id) {
        super("REPUBLISH author:@"+user+" msg_id:"+id);
    }
}
