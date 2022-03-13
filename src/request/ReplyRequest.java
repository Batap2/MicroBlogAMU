package request;

public class ReplyRequest extends RequestWithBody {

    public ReplyRequest(String user, String id, String message) {

        super("REPLY author:@"+user+" reply_to_id:"+id, message);
    }

}
