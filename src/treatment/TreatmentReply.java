package treatment;

import request.ReplyRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

public class TreatmentReply extends Treatment {

    private String pseudo;
    private String id;
    private String message;

    public TreatmentReply(String pseudo, String id, String message){
        this.pseudo = pseudo;
        this.id = id;
        this.message = message;
    }

    @Override
    public String treatment(OutputStream outputStream, BufferedReader inputStream) throws IOException {
        ReplyRequest request = new ReplyRequest(pseudo, id, message);
        String message = request.getEntete()+request.getBody();
        byte[] msg = message.getBytes();

        outputStream.write(msg);

        String reponse = inputStream.readLine();
        System.out.println("Reponse : " + reponse);
        return reponse;
    }
}
