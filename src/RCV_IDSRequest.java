import java.util.ArrayList;

public class RCV_IDSRequest extends Request {


    public RCV_IDSRequest(ArrayList<String> parameters) {
        super("RCV_IDS author:"+parameters.get(0)+" tag:"+parameters.get(1)+" since_id:"+parameters.get(2)+" limit:"+parameters.get(3)+"\r\n");
    }
}
