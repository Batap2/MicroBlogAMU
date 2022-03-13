import treatment.TreatmentRCV_IDS;
import treatment.TreatmentRepublish;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientRepost extends ClientWithIdentification {

    private String pseudo;
    private TreatmentRCV_IDS treatment;
    private TreatmentRepublish nextTreatment;

    public static void main(String args[]) throws IOException {

        ClientRepost clientRepost = new ClientRepost();
        Socket socket = new Socket();
        socket = clientRepost.connection(socket);

        BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        OutputStream outputStream = socket.getOutputStream();

        Scanner keyboard = new Scanner(System.in);
        clientRepost.pseudo = clientRepost.identification(keyboard);
        clientRepost.nextTreatment = new TreatmentRepublish(clientRepost.pseudo);

        System.out.println("De qui voulez-vous republier les messages ?");
        System.out.println("Format : @user\n");

        while(keyboard.hasNextLine()){

            String message = keyboard.nextLine();
            clientRepost.treatment = new TreatmentRCV_IDS(message);
            ArrayList<String> parameters = clientRepost.treatment.getParameters();

            if(parameters.get(1) != null || parameters.get(2) != null || parameters.get(3) != null){
                System.out.println("erreur de format");
            }

            clientRepost.treatment.treatment(outputStream, inputStream, clientRepost.nextTreatment);

        }



    }

}
