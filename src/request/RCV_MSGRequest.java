package request;

import request.Request;

public class RCV_MSGRequest extends Request {


    public RCV_MSGRequest(String id) {
        super("RCV_MSG msg_id:"+id);
    }


}
