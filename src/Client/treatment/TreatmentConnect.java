package client.treatment;

import client.request.ConnectRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

public class TreatmentConnect extends Treatment {

    private String pseudo;

    public TreatmentConnect(String pseudo){
        this.pseudo = pseudo;
    }

    @Override
    public String treatment(OutputStream outputStream, BufferedReader inputStream) throws IOException, InterruptedException {

        ConnectRequest request = new ConnectRequest(pseudo);

        String message = request.getEntete();
        byte[] msg = message.getBytes();

        outputStream.write(msg);

        String receivedMessage = "";
        Thread.sleep(100);
        while(inputStream.ready()){
            receivedMessage = receivedMessage + " "+inputStream.readLine()+"\n";
        }

        System.out.println(receivedMessage);

        return receivedMessage;
    }
}
