public class Message {
    private int ident;
    private String author;
    private String body;

    public Message(int ident, String author, String body){
        this.ident = ident;
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

    @Override
    public String toString(){
        return "Msg ID : " + getIdent() + ", Author:" + getAuthor() +"\r\n" + getBody();
    }
}
