package client.request;

public class PublishRequest extends RequestWithBody {


    public PublishRequest(String pseudo, String body){
        super("PUBLISH author:@"+pseudo, body);

    }


}
