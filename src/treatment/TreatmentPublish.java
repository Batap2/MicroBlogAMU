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
        System.out.println("Reponse : " + reponse);

        return reponse;
    }

}
