import treatment.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class  ClientMicroblogamu extends ClientWithIdentification {

    private String pseudo;
    private TreatmentPublish traitementPublish;
    private TreatmentRepublish treatmentRepublish;
    private TreatmentReply treatmentReply;
    private TreatmentSubscribe treatmentSubscribe;

    public static void main(String args[]) throws IOException, InterruptedException {

        ClientMicroblogamu client = new ClientMicroblogamu();
        Socket socket = new Socket();
        socket = client.connection(socket);

        BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        OutputStream outputStream = socket.getOutputStream();

        Scanner keyboard = new Scanner(System.in);

        client.pseudo = client.connection(keyboard, outputStream, inputStream);

        System.out.println("Choisissez la requête");
        System.out.println("Format : PUBLISH || REPLY || REPUBLISH || SUBSCRIBE || UNSUBSCRIBE \n");

        while(keyboard.hasNextLine()){
            String requestChoice = keyboard.nextLine();

            if(requestChoice.equals("PUBLISH")){
                System.out.println("Vous pouvez désormais entrer votre message");
                String message = keyboard.nextLine();
                client.traitementPublish = new TreatmentPublish(client.pseudo, message);
                client.traitementPublish.treatment(outputStream, inputStream);

            }
            else if(requestChoice.equals("REPLY")){
                System.out.println("Saississez l'id du message que vous voulez reposter");
                String id = keyboard.nextLine();
                System.out.println("Vous pouvez désormais entrer votre message");
                String message = keyboard.nextLine();
                client.treatmentReply = new TreatmentReply(client.pseudo, id, message);
                client.treatmentReply.treatment(outputStream, inputStream);

            }
            else if(requestChoice.equals("REPUBLISH")){
                System.out.println("Saississez l'id du message que vous voulez reposter");
                String id = keyboard.nextLine();
                client.treatmentRepublish = new TreatmentRepublish(client.pseudo, id);
                client.treatmentRepublish.treatment(outputStream, inputStream);
            }
            else if(requestChoice.equals("SUBSCRIBE") || requestChoice.equals("UNSUBSCRIBE")){
                System.out.println("Format : @user || #tag");
                String parameter = keyboard.nextLine();
                client.treatmentSubscribe = new TreatmentSubscribe(requestChoice, parameter);
                client.treatmentSubscribe.treatment(outputStream, inputStream);

            }
            else{
                System.out.println("erreur de format");
            }

        }


    }

}
