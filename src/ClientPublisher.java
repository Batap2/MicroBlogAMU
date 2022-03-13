import treatment.TreatmentPublish;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientPublisher extends ClientWithIdentification{

    private String pseudo;
    private TreatmentPublish traitementPublish;

    public static void main(String args[]) throws IOException, InterruptedException {

        ClientPublisher client = new ClientPublisher();
        Socket socket = new Socket();
        socket = client.connection(socket);


        BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        OutputStream outputStream = socket.getOutputStream();

        Scanner keyboard = new Scanner(System.in);

        //client.pseudo = client.identification(keyboard);
        client.pseudo = client.connection(keyboard, outputStream, inputStream);

        System.out.println("Vous pouvez désormais entrer vos messages");

        while(keyboard.hasNext()){

            client.traitementPublish = new TreatmentPublish(client.pseudo, keyboard.nextLine());
            client.traitementPublish.treatment(outputStream, inputStream);
        }


    }

}
