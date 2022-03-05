import java.io.IOException;
import java.util.Scanner;

public class ClientFollower extends Client{

    private static String getOption(String message, int length){
        int index = message.indexOf(" ");
        String option = message.substring(0, index+1);
        message = message.substring(index+1, length);
        return option;
    }

    private static int majLength(String message, int length){
        length = message.length();
        return length;
    }

    public static void main(String args[]) throws IOException {

        ClientFollower clientFollower = new ClientFollower();
        clientFollower.connection();

        Scanner keyBoard = new Scanner(System.in);
        System.out.println("De qui voulez-vous récupérer les messages ?");
        System.out.println("Format : @user [#tag] [id] [limit]");
        String tag;
        String id;
        String limit;

        while(keyBoard.hasNextLine()){
            String message = keyBoard.nextLine();
            int length = message.length();
            if(message.contains(" ")){
                int index = message.indexOf(" ");
                String author = getOption(message, length);
                length = majLength(message, length);

                if(message.charAt(0) == '#'){
                    if(message.contains(" ")){
                        int index2 = message.indexOf(" ");
                        tag = message.substring(0, index2+1);
                        message = message.substring(index2+1, length);
                        length = message.length();
                    }
                }


            }
            else{ //il y a juste le nom de l'auteur

            }


        }

    }



}
