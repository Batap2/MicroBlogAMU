package Server;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    private int ident;
    private String author;
    private String body;
    private ArrayList<String> tagList;
    private int reply_to_id = -1;
    private boolean isRepublished = false;

    public Message(int ident, String author, String body){
        this.ident = ident;
        this.author = author;
        this.body = body;
        tagList = recoTag();
    }
    public Message(int ident, String author, String body, int reply_to_id){
        this.ident = ident;
        this.author = author;
        this.body = body;
        this.reply_to_id = reply_to_id;
        tagList = recoTag();
    }

    public Message(int ident, String author, String body, boolean isRepublished){
        this.ident = ident;
        this.author = author;
        this.body = body;
        this.isRepublished = isRepublished;
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

    public int getReply_to_id(){
        return reply_to_id;
    }

    public boolean isRepublished() {
        return isRepublished;
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

    public String getEnTete(){
        String str = "author:" + getAuthor() + " msg_id: " + getIdent();
        if(reply_to_id != -1){
            str = str + " reply_to_id:" + getReply_to_id();
        }
        if(isRepublished){
            str = str + " republished:true";
        }
        return str;
    }
    @Override
    public String toString(){
        return getEnTete() + "\r\n" + getBody() + "\r\n";
    }
}
