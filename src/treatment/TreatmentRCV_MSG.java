package treatment;

import request.RCV_MSGRequest;
import treatment.Treatment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

public class TreatmentRCV_MSG extends Treatment {

    private String id;

    public TreatmentRCV_MSG(String id){
        this.id = id;
    }

    public TreatmentRCV_MSG(){

    }


    public String treatment(OutputStream outputStream, BufferedReader inputStream) throws IOException {

        System.out.println("Messages correspondant à la demande :");

        RCV_MSGRequest request2 = new RCV_MSGRequest(id);
        byte[] message2 = request2.getEntete().getBytes();

        outputStream.write(message2);

        String response2 = inputStream.readLine();
        String receivedMessage = "";
        if(!response2.equals("MSG")){
            System.out.println("erreur");
        }
        else{

            while(inputStream.ready()){
                receivedMessage = receivedMessage + " "+inputStream.readLine()+"\r\n";
            }
        }
        System.out.println(receivedMessage);
        return receivedMessage;
    }

    void setID(String attribute) {
        id = attribute;
    }

}
