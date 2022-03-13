package request;

public class SubscribeUnsubscribeRequest extends Request{

    public SubscribeUnsubscribeRequest(String requestName, String parameter) {
        super(requestName);
        setEntete(parameter);
    }

    public void setEntete(String parameter){
        String entete = super.getEntete().substring(0, super.getEntete().indexOf("\n")-1);
        if(parameter.charAt(0) == '@'){
            entete = entete +" author:"+parameter;
        }
        else if(parameter.charAt(0) =='#'){
            entete = entete+" tag:"+parameter;
        }
        else{
            System.out.println("erreur de format");
        }
        super.setEntete(entete);
    }



}
