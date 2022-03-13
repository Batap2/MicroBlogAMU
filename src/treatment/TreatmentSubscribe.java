package treatment;

import request.SubscribeUnsubscribeRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

public class TreatmentSubscribe extends Treatment {

    private String requestName;
    private String parameter;

    public TreatmentSubscribe(String requestName, String parameter){
        this.parameter = parameter;
        this.requestName = requestName;
    }

    @Override
    public String treatment(OutputStream outputStream, BufferedReader inputStream) throws IOException {
        SubscribeUnsubscribeRequest request = new SubscribeUnsubscribeRequest(requestName, parameter);
        String message = request.getEntete();
        byte[] msg = message.getBytes();

        outputStream.write(msg);

        String reponse = inputStream.readLine();
        /*if(reponse.equals("OK") || reponse.equals("ERROR")){
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
        }*/

        System.out.println(reponse);
        return reponse;
    }

}
