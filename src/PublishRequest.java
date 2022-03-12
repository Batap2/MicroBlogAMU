import java.util.ArrayList;

public class PublishRequest extends RequestWithBody {


    public PublishRequest(String pseudo, String body){
        super("PUBLISH author:@"+pseudo+"\r\n", body);

    }


}
