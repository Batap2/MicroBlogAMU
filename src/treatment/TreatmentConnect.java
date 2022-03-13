package treatment;

import request.ConnectRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

public class TreatmentConnect extends Treatment {

    private String pseudo;

    public TreatmentConnect(String pseudo){
        this.pseudo = pseudo;
    }

    @Override
    public String treatment(OutputStream outputStream, BufferedReader inputStream) throws IOException {

        ConnectRequest request = new ConnectRequest(pseudo);

        String message = request.getEntete();
        byte[] msg = message.getBytes();

        outputStream.write(msg);

        String receivedMessage = "";
        while(inputStream.ready()){
            receivedMessage = receivedMessage + " "+inputStream.readLine()+"\n";
        }

        System.out.println(receivedMessage);

        return receivedMessage;
    }
}
