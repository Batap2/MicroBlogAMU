import java.util.Scanner;

abstract public class ClientWithIdentification extends  Client {

    public String identification(Scanner keyboard){

        System.out.println("Veuillez entrer votre nom d'utilisateur");
        String pseudo = keyboard.nextLine();
        System.out.println("Bienvenue @"+pseudo);
        return pseudo;

    }

}
