package treatment;

import request.PublishRequest;
import treatment.Treatment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

public class TreatmentPublish extends Treatment {

    private String pseudo;
    private String message;

    public TreatmentPublish(String pseudo, String message){
        this.pseudo = pseudo;
        this.message = message;
    }

    public String treatment(OutputStream outputStream, BufferedReader inputStream) throws IOException {

        PublishRequest publishRequest = new PublishRequest(pseudo, message);

        String message = publishRequest.getEntete()+publishRequest.getBody();
        byte[] msg = message.getBytes();

        outputStream.write(msg);

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
