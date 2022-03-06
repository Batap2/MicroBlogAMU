public class Message {
    private int ident;
    private String author;
    private String body;

    public Message(String author, String body){
        Server.db.setLastMsgID(Server.db.getLastMsgID() + 1);
        ident = Server.db.getLastMsgID();
        this.author = author;
        this.body = body;
    }

    public int getIdent() {
        return ident;
    }

    public String getBody() {
        return body;
    }

    public String getAuthor() {
        return author;
    }
}
