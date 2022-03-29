package client;
import client.treatment.TreatmentRCV_IDS;
import client.treatment.TreatmentRCV_MSG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientFollower extends Client{

    private TreatmentRCV_IDS treatment;
    private TreatmentRCV_MSG nextTreatment = new TreatmentRCV_MSG();

    public static void main(String args[]) throws IOException {

        ClientFollower clientFollower = new ClientFollower();
        Socket socket = new Socket();
        socket = clientFollower.connection(socket);

        BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        OutputStream outputStream = socket.getOutputStream();

        Scanner keyBoard = new Scanner(System.in);
        System.out.println("De qui voulez-vous récupérer les messages ?");
        System.out.println("Format : @user [#tag] [id:id] [limit]\n");

        while(keyBoard.hasNextLine()){

            String message = keyBoard.nextLine();
            clientFollower.treatment = new TreatmentRCV_IDS(message);
            ArrayList<String> parameters = clientFollower.treatment.getParameters();

            if(parameters.get(0).charAt(0) != '@'){
                System.out.println("erreur de format");
            }

            clientFollower.treatment.treatment(outputStream, inputStream, clientFollower.nextTreatment);


        }

    }

}


