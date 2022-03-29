package client.request;

abstract public class Request {

    private String entete;

    public Request(String entete){
        this.entete = entete+"\r\n";
    }

    public String getEntete(){
        return entete;
    }

    public void setEntete(String entete){
        this.entete = entete+"\r\n";
    }

}
