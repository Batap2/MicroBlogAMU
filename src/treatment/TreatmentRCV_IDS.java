package treatment;

import request.RCV_IDSRequest;
import treatment.Treatment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class TreatmentRCV_IDS extends Treatment {

    private String input;
    private ArrayList<String> parameters = new ArrayList<>();

    public TreatmentRCV_IDS(String input){
        this.input = input;
    }

    private String getOption(){
        String option;
        int index;
        if(input.contains(" ")){
            index = input.indexOf(" ");
            option = input.substring(0, index);
            input = input.substring(index+1);
        }
        else{
            option = input;
            input = "";
        }
        return option;
    }

    public ArrayList<String> getParameters(){

        ArrayList<String> parameters = new ArrayList<>();
        if(input.charAt(0) == '@'){
            parameters.add(getOption());
        }
        else{
            parameters.add(null);
        }
        if(input.length() != 0 && input.charAt(0) == '#'){
            parameters.add(getOption());
        }
        else{
            parameters.add(null);
        }
        if(input.length() != 0 && input.contains("id:")){
            input = input.substring(3);
            parameters.add(getOption());
        }
        else{
            parameters.add(null);
        }
        if(input.length() != 0){
            parameters.add(getOption());
        }
        else{
            parameters.add(null);
        }
        this.parameters = parameters;
        return parameters;
    }

    public String treatment(OutputStream outputStream, BufferedReader inputStream) throws IOException {
        RCV_IDSRequest request = new RCV_IDSRequest(parameters);
        byte[] msg = request.getEntete().getBytes();

        outputStream.write(msg);

        String allID = "";
        String reponse = inputStream.readLine();

        /*if(reponse.equals("MSG")){
            String receivedMessage = "";
            while(inputStream.ready()){
                receivedMessage = receivedMessage + " "+inputStream.readLine()+"\r\n";
            }
            System.out.println(receivedMessage);
        }
        else if(reponse.equals("MSG_IDS")){
            while(inputStream.ready()){
                allID = inputStream.readLine();
            }
        }
        else{
            System.out.println("erreur");
        }*/

        if(!reponse.equals("MSG_IDS")){
            System.out.println("erreur");
        }
        else{
            while(inputStream.ready()){
                allID = inputStream.readLine();
            }
        }

        System.out.println(allID);
        return allID;
    }


    //todo : fusionner les deux methodes suivantes

    public void treatment(OutputStream outputStream, BufferedReader inputStream, TreatmentRCV_MSG nextTreatment) throws IOException {

        String allID = treatment(outputStream, inputStream);
        this.input = allID;
        while(input.length() != 0){
            String id = getOption();
            TreatmentRCV_MSG treatmentRCV_msg = new TreatmentRCV_MSG(id);
            treatmentRCV_msg.treatment(outputStream, inputStream);
        }

    }

    public void treatment(OutputStream outputStream, BufferedReader inputStream, TreatmentRepublish nextTreatment) throws IOException {

        String allID = treatment(outputStream, inputStream);
        this.input = allID;
        while(input.length() != 0){
            String id = getOption();
            TreatmentRepublish treatmentRCV_msg = new TreatmentRepublish(nextTreatment.getPseudo(), id);
            treatmentRCV_msg.treatment(outputStream, inputStream);
        }

    }

}
