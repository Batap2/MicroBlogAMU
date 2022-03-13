package request;

import request.RequestWithBody;

public class PublishRequest extends RequestWithBody {


    public PublishRequest(String pseudo, String body){
        super("PUBLISH author:@"+pseudo, body);

    }


}
