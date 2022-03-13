package request;

import request.Request;

abstract public class RequestWithBody extends Request {

    private String body;

    public RequestWithBody(String entete, String body) {
        super(entete);
        this.body = body+ "\r\n";
    }

    public String getBody(){
        return body;
    }


}
