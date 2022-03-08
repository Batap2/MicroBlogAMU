import java.util.ArrayList;

public class Message {
    private int ident;
    private String author;
    private String body;
    private ArrayList<String> tagList;

    public Message(int ident, String author, String body){
        this.ident = ident;
        this.author = author;
        this.body = body;
        tagList = recoTag();
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

    public ArrayList<String> getTagList() {
        return tagList;
    }

    private ArrayList<String> recoTag(){
        ArrayList<String> tags = new ArrayList<>();
        String body = getBody();
        int i = 0;
        while(i < body.length()){
            if(body.charAt(i) == '#'){
                StringBuilder tag = new StringBuilder();
                while(i < body.length() && body.charAt(i) != ' '){
                    tag.append(body.charAt(i));
                    i++;
                }
                tags.add(tag.toString());
            }
            i++;
        }
        return tags;
    }

    @Override
    public String toString(){
        return "Msg ID : " + getIdent() + ", Author:" + getAuthor() +"\r\n" + getBody();
    }
}
