package Server;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Test {
    public static void main(String args[]) throws IOException, ClassNotFoundException {
        String line = "@batap 1 2 3 983 167";

        ArrayList<Integer> ids = new ArrayList<>();

        int i = line.indexOf(' ') + 1;
        while (i < line.length()){
            StringBuilder id = new StringBuilder();
            while(i < line.length() && line.charAt(i) != ' '){
                id.append(line.charAt(i));
                i++;
            }
            i++;
            ids.add(Integer.parseInt(id.toString()));
        }
        for(Integer i2 : ids){
            System.out.println(i2);
        }
    }
}
