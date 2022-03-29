package client;

import client.treatment.TreatmentConnect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

abstract public class ClientWithIdentification extends Client {

    public String identification(Scanner keyboard){

        System.out.println("Veuillez entrer votre nom d'utilisateur");
        String pseudo = keyboard.nextLine();
        System.out.println("Bienvenue @"+pseudo);
        return pseudo;

    }

    public String connection(Scanner keyboard, OutputStream outputStream, BufferedReader inputStream) throws IOException, InterruptedException {
        String pseudo = identification(keyboard);
        TreatmentConnect treatmentConnect = new TreatmentConnect(pseudo);
        treatmentConnect.treatment(outputStream, inputStream);
        return pseudo;
    }

}
