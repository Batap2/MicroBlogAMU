public class PublishRequest {
    public static int lastIdent = 0;
    private int ident;
    private String author;
    private String body;

    public PublishRequest(String author, String body){
        ident = lastIdent;
        lastIdent++;
        this.body = body;
    }

    public int getIdent() {
        return ident;
    }

    public String getBody() {
        return body;
    }
}
