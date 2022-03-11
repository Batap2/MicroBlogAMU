import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Test {
    public static void main(String args[]) throws IOException {
        String[] subs = {"@mwaka", "@sable"};
        Subscription sub = new Subscription("aa", subs);
        System.out.println(sub.getSubscribersStr() + "]");
    }
}
