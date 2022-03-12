import java.util.ArrayList;

abstract public class Request {

    private String entete;

    public Request(String entete){
        this.entete = entete;
    }

    public String getEntete(){
        return entete;
    }


}
