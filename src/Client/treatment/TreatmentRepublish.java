package client.treatment;

import client.request.RepublishRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

public class TreatmentRepublish extends Treatment {

    String pseudo;
    String id;

    public TreatmentRepublish(String pseudo){
        this.pseudo = pseudo;
    }

    public TreatmentRepublish(String pseudo, String id){
        this.pseudo = pseudo;
        this.id = id;
    }

    public String getPseudo(){
        return pseudo;
    }

    @Override
    public String treatment(OutputStream outputStream, BufferedReader inputStream) throws IOException {
        RepublishRequest requete = new RepublishRequest(pseudo, id);
        byte[] message2 = requete.getEntete().getBytes();

        outputStream.write(message2);

        String reponse = inputStream.readLine();
        if(reponse.equals("OK") || reponse.equals("ERROR")){
            System.out.println("Reponse : " + reponse);
        }
        else if(reponse.equals("MSG")){
            String receivedMessage = "";
            while(inputStream.ready()){
                receivedMessage = receivedMessage + " "+inputStream.readLine()+"\r\n";
            }
            System.out.println(receivedMessage);
        }
        else{
            System.out.println("erreur");
        }

        //System.out.println(reponse);

        return reponse;
    }



}
