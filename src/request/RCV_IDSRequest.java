package request;

import request.Request;

import java.util.ArrayList;

public class RCV_IDSRequest extends Request {

    public RCV_IDSRequest(ArrayList<String> parameters) {
        super("RCV_IDS");
        setEntete(parameters);
    }

    public RCV_IDSRequest(){
        super("RCV_IDS");
    }

    public void setEntete(ArrayList<String> parameters){
        if(parameters.size() == 4){
            String entete = super.getEntete().substring(0, super.getEntete().indexOf("\n")-1);
            if(parameters.get(0) != null){
                entete = entete+" author:"+parameters.get(0);
            }
            if(parameters.get(1) != null){
                entete = entete+" tag:"+parameters.get(1);
            }
            if(parameters.get(2) != null){
                entete = entete+" since_id:"+parameters.get(2);
            }
            if(parameters.get(3) != null){
                entete = entete+" limit:"+parameters.get(3);
            }
            super.setEntete(entete);
        }
        else{
            System.out.println("l'argument parameters ne fait pas la bonne taille");
        }


    }
}
